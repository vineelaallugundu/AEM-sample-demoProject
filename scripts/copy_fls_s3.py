#!/usr/bin/python3


import argparse
import boto3
import os.path


################################################################################
# FUNCTION NAME: get_aws_creds
#
# DESCRIPTION: Assume role and create AWS session
################################################################################
def get_aws_creds(aws_account_id, aws_requested_role, aws_region):

   # create an STS client object that represents a live connection to the
   # STS service
   sts_client = boto3.client('sts')

   # Get the current role arn
   response = sts_client.get_caller_identity()

   # Call the assume_role method of the STSConnection object and pass the role
   # ARN and a role session name.
   assumed_role_object=sts_client.assume_role(
      RoleArn="arn:aws:iam::{0}:role/{1}".format(aws_account_id, aws_requested_role),
      RoleSessionName="Rollback-secrets-session"
   )

   # From the response that contains the assumed role, get the temporary
   # credentials that can be used to make subsequent API calls
   credentials=assumed_role_object['Credentials']

   # Create the session
   session = boto3.Session(
      aws_access_key_id=credentials['AccessKeyId'],
      aws_secret_access_key=credentials['SecretAccessKey'],
      aws_session_token=credentials['SessionToken'],
      region_name=aws_region
   )

   return session


################################################################################
# FUNCTION NAME: copy_fl
#
# DESCRIPTION: Copy file to S3
################################################################################
def copy_fl(session, files, bucket, path):

 #  # Prior to copying anything let's make sure ALL of the files exist locally
 #  for fl in files:
 #     for name in fl:
 #        if not os.path.isfile(name):
 #           raise Exception("ERROR - file doesn't exist: {}".format(name))

   # Copy each of the files to the S3 bucket
   s3_assumed = session.client('s3')
   for fl in files:
      for name in fl:
         dest = ""
         if path:
            dest += path + '/'
         dest += os.path.basename(name)
         print( "Copying file: '{0}' to S3 bucket '{1}/{2}'".format(name, 
            bucket, dest))
         s3_assumed.upload_file(name, bucket, dest)

   return


################################################################################
################################################################################
# MAIN
################################################################################
################################################################################
if __name__ == '__main__':
   # Parse the command line args
   parser = argparse.ArgumentParser()
   parser.add_argument('-a', '--awsrole',
                       help="The AWS role to request",
                       required=True)
   parser.add_argument('-b', '--bucket',
                       help="The name of the S3 bucket",
                       required=True)
   parser.add_argument('-f', '--file', action='append', nargs='+',
                       help="The file to copy to the S3 bucket",
                       required=True)
   parser.add_argument('-i', '--awsid',
                       help="The AWS Account ID",
                       required=True)
   parser.add_argument('-p', '--path',
                       help="The path to copy the files to")
   parser.add_argument('-r', '--region',
                       help="The AWS Region that the S3 bucket is in",
                       required=True)
   args = parser.parse_args()

   # Assume AWS role and get login creds
   session = get_aws_creds(args.awsid, args.awsrole, args.region)

   # Copy the file to S3
   copy_fl(session, args.file, args.bucket, args.path)
