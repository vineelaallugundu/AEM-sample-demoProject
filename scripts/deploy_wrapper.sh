#!/bin/bash


# Declare global vars
AWS_ACCOUNT_ID=
AWS_REGION=
ENDPOINT_URL=
AWS_REQUESTED_ROLE=
DEPL_CMD=


################################################################################
# FUNCTION NAME: usage
#
# DESCRIPTION: Display the usage statement and exit
################################################################################
usage() {
   ec=$1

   echo "USAGE: $(basename $0) -a <ACCOUNT_ID> -c <COMMAND> -g <REGION> -p <EPURL> -r <ROLE> [-h]
   where:
      -a <ACCOUNT_ID> : The AWS account ID to assume role to
      -c <COMMAND>    : The deploy command to run after assuming role
      -g <REGION>     : The AWS region
      -p <EPURL>      : The endpoint URL to use
      -r <ROLE>       : The AWS role to request
      -h              : Displays this message
   "

   exit $ec

} # end usage

################################################################################
# FUNCTION NAME: enforce_required_input
#
# DESCRIPTION: Make sure the required paramaters were passed in
################################################################################
enforce_required_input() {

   if [[ -z "$AWS_ACCOUNT_ID" ]]; then
      msg+="ACCOUNT_ID not populated. Please pass this as -a argument.\n"
   fi

   if [[ -z "$DEPL_CMD" ]]; then
      msg+="COMMAND not populated. Please pass this as -c argument.\n"
   fi

   if [[ -z "$AWS_REGION" ]]; then
      msg+="REGION not populated. Please pass this as -g argument.\n"
   fi

   if [[ -z "$ENDPOINT_URL" ]]; then
      msg+="EPURL not populated. Please pass this as -p argument.\n"
   fi

   if [[ -z "$AWS_REQUESTED_ROLE" ]]; then
      msg+="ROLE not populated. Please pass this as -r argument.\n"
   fi

   # If there's a message, print it and display the usage statement
   if [[ ! -z "$msg" ]]; then
      err $msg
      usage 1
   fi

} # end enforce_required_input

################################################################################
################################################################################
# MAIN
################################################################################
################################################################################

# Get the deployment helper script
cmd="wget --no-check-certificate https://gitlab.global.dish.com/it-wireless-commercial-application-engineering-repos/centralized-deployment-automation/deployment-scripts/generic-cd-scripts/-/raw/master/helpers/deployment-helper-library.sh -O deployment-helper-library.sh"
echo "Running: '$cmd'"
$cmd
rc=$?
if [ $rc -ne 0 ]
then
   echo "Failed to wget the deployment-helper-library.sh script"
   exit $rc
fi
chmod -R 755 deployment-helper-library.sh
source ./deployment-helper-library.sh

# Parse the command line arguments
while getopts "a:c:g:p:r:h" arg; do
   case "${arg}" in
      a)
         AWS_ACCOUNT_ID=${OPTARG}
         ;;
      c)
         DEPL_CMD=${OPTARG}
         ;;
      g)
         AWS_REGION=${OPTARG}
         ;;
      p)
         ENDPOINT_URL=${OPTARG}
         ;;
      r)
         AWS_REQUESTED_ROLE=${OPTARG}
         ;;
      h)
         usage 0
         ;;
      *)
         err "Invalid argument - '${OPTARG}'"
         usage 1
         ;;
   esac
done

# Make sure the required command line args were passed in
enforce_required_input

# Get the AWS helper script and source it
helper_depl_run_cmd wget --no-check-certificate https://gitlab.global.dish.com/it-wireless-commercial-application-engineering-repos/centralized-deployment-automation/deployment-scripts/generic-cd-scripts/-/raw/master/helpers/aws-helper-library.sh -O aws-helper-library.sh
helper_depl_run_cmd chmod -R 755 aws-helper-library.sh
helper_depl_run_cmd source ./aws-helper-library.sh

# Assume the AWS role
assume_role "$AWS_ACCOUNT_ID" "$AWS_REQUESTED_ROLE" "Update-AEM-Settings_File" "$ENDPOINT_URL" 0

# Run the deploy command
helper_depl_run_cmd cd ..
helper_depl_run_cmd $DEPL_CMD
