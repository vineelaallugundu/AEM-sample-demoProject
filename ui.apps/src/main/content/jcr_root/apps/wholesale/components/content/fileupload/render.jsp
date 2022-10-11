<%--
  ADOBE CONFIDENTIAL

  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.ArrayList,
                  java.util.HashMap,
                  java.util.List,
                  javax.jcr.Node,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.servlets.post.SlingPostConstants,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.Value,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.commons.jcr.JcrConstants,
                  com.day.cq.dam.api.Asset,
                  com.day.cq.dam.commons.util.UIHelper,
                  com.day.cq.wcm.api.policies.ContentPolicy,
                  com.day.cq.wcm.api.policies.ContentPolicyManager,
                  com.day.cq.wcm.resource.details.AssetDetails" %><%--###
FileUpload
==========

   A field component for uploading or selecting files from an authoring dialog context.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.

   It has the following content structure:

   .. gnd:gnd::

      [author:DialogFileUpload] > granite:FormField

      /**
       * The name that identifies the file upload location. E.g. ./file or ./image/file
       */
      - name (String)

      /**
       * Indicates if the field is in a disabled state.
       */
      - disabled (Boolean)

      /**
       * Indicates if it is mandatory to complete the field.
       */
      - required (Boolean)

      /**
       * The name of the validator to be applied. E.g. ``foundation.jcr.name``.
       * See :doc:`validation </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/validation/index>` in Granite UI.
       */
      - validation (String) multiple

      /**
       * The file size limit.
       */
      - sizeLimit (Long)

      /**
       * The browse and selection filter for file selection. E.g. [".png",".jpg"] or ["image/\*"].
       */
      - mimeTypes (String) multiple

      /**
       * The icon.
       */
      - icon (String)

      /**
       * The location for storing the name of the file. E.g. ./fileName or ./image/fileName
       */
      - fileNameParameter (String)

      /**
       * The location for storing a DAM file reference. E.g. ./fileReference or ./image/fileReference
       */
      - fileReferenceParameter (String)

      /**
       * Indicates whether upload from local file system is allowed.
       */
      - allowUpload (Boolean) = 'true'

      /**
      * The URI Template used for editing the DAM file referenced.
      */
      - viewInAdminURI (String) = '/assetdetails.html{+item}'


###--%><%
    final String FILE_NAME = "fileName";
    final String FILE_REFERENCE = "fileReference";
    final String TEMPORARY_FILE_SUFFIX = ".sftmp";

    Config cfg = cmp.getConfig();
    Tag tag = cmp.consumeTag();

    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    String name = cfg.get("name", String.class);

    // checks for an existing temporary file suffix on the name and removes it
    if (name.length() >= TEMPORARY_FILE_SUFFIX.length() &&
        name.substring(name.length() - TEMPORARY_FILE_SUFFIX.length()).equals(TEMPORARY_FILE_SUFFIX)) {
        name = name.substring(0, name.length() - TEMPORARY_FILE_SUFFIX.length());
    }

    attrs.add("name", name);
    attrs.add("sizelimit", cfg.get("sizeLimit", String.class));
    attrs.addDisabled(cfg.get("disabled", false));
    attrs.addBoolean("data-cq-fileupload-required", cfg.get("required", false));
    attrs.addBoolean("async", true);
    attrs.addClass("cq-FileUpload cq-droptarget");

    String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
    attrs.add("data-foundation-validation", validation);

    String[] mimeTypes = cfg.get("mimeTypes", new String[0]);
    if (mimeTypes.length > 0) {
        attrs.add("accept", StringUtils.join(mimeTypes, ","));
    }

    String contextResourcePath = slingRequest.getRequestPathInfo().getSuffix();
    String action = (String)request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);

    if (StringUtils.isEmpty(contextResourcePath)) {
        contextResourcePath = StringUtils.defaultIfEmpty(action, StringUtils.EMPTY);
    }

    // Relative path to the content resource holding the file metadata
    String fileHolderPath = (name.lastIndexOf("/") >= 0) ? name.substring(0, name.lastIndexOf("/") + 1) : "";

    String fileName = cfg.get("fileNameParameter", fileHolderPath + FILE_NAME);
    String fileReference = cfg.get("fileReferenceParameter", fileHolderPath + FILE_REFERENCE);
    String fileDelete = name + SlingPostConstants.SUFFIX_DELETE;
    String fileMoveFrom = name + SlingPostConstants.SUFFIX_MOVE_FROM;
    String lastModified = fileHolderPath + JcrConstants.JCR_LASTMODIFIED;
    String lastModifiedBy = fileHolderPath + JcrConstants.JCR_LAST_MODIFIED_BY;
    String temporaryFileName;
    if (action != null && !StringUtils.equals(contextResourcePath, action)) {
        String compositePath = action.substring(contextResourcePath.length());
        compositePath = compositePath.replaceAll("\\/", "_");
        temporaryFileName = name + TEMPORARY_FILE_SUFFIX + compositePath;
    } else {
        temporaryFileName = name + TEMPORARY_FILE_SUFFIX;
    }

    String temporaryFileDelete = temporaryFileName + SlingPostConstants.SUFFIX_DELETE;
    String temporaryFilePath = contextResourcePath + propertyToAbsolutePath(temporaryFileName);
    String thumbnailSrc = action + propertyToAbsolutePath(fileHolderPath);
    String thumbnailTitle = null;
    String thumbnailAlt = null;
    String resourceFileName = null;
    String resourceFilePath = null;
    Resource assetResource = null;
    boolean isAssetFromDAM = false;

    temporaryFilePath.replace("_jcr_content", JcrConstants.JCR_CONTENT).replace("^" + request.getContextPath(), "");
    attrs.addHref("action", contextResourcePath);
    attrs.add("data-cq-fileupload-temporaryfilename", temporaryFileName);
    attrs.add("data-cq-fileupload-temporaryfiledelete", temporaryFileDelete);
    attrs.add("data-cq-fileupload-temporaryfilepath", temporaryFilePath);

    Resource res = resourceResolver.getResource(action);

    // check if upload is enabled
    boolean allowUpload = isAllowed("allowUpload", cfg, res, resourceResolver);
    if (allowUpload) {
        attrs.add("data-cq-fileupload-allowupload", "");
    }

    // check if drop is enabled
    boolean allowDrop = isAllowed("allowDrop", cfg, res, resourceResolver);
    if (allowDrop) {
        attrs.add("data-cq-fileupload-allowdrop", "");
    }

    if (res != null) {
        ValueMap vm = res.adaptTo(ValueMap.class);
        resourceFileName = vm.get(fileName, String.class);
        resourceFilePath = vm.get(fileReference, String.class);

        // Resolution of the asset resource
        if (StringUtils.isNotEmpty(resourceFilePath)) {
            assetResource = resourceResolver.getResource(resourceFilePath);
        }

        if (assetResource == null) {
            assetResource = res.getChild(fileHolderPath);
        }

        // Fallback to the resource file path
        if (assetResource != null && StringUtils.isEmpty(resourceFileName)) {
            resourceFileName = assetResource.getPath();
        }

        if (assetResource != null) {
            Node thumbnailNode = res.adaptTo(Node.class);
            long cacheKiller = UIHelper.getCacheKiller(thumbnailNode);
            Asset asset = assetResource.adaptTo(Asset.class);

            if (asset != null) {
                isAssetFromDAM = true;
                AssetDetails assetDetails = new AssetDetails(assetResource);
                thumbnailSrc = request.getContextPath() + assetDetails.getThumbnailUrl() + "?ch_ck=" + cacheKiller;
                thumbnailAlt = assetDetails.getDescription();
            } else {
                // image has been uploaded
                thumbnailSrc = thumbnailSrc + getThumbnailSuffix(res, name) + "?ch_ck=" + cacheKiller;
                thumbnailAlt = UIHelper.getTitle(assetResource);
            }
        }

        if (StringUtils.isNotEmpty(resourceFileName)) {
            thumbnailTitle = resourceFileName;
        }
    }

    // Either the asset exist in DAM
    // Or is located in the content
    boolean assetExists = isAssetFromDAM || res != null && res.getChild(name) != null;

    if (!allowDrop || assetExists) {
        attrs.addClass("is-filled");
    }

    ValueMap fieldVM = (ValueMap) request.getAttribute(Field.class.getName());
    String fieldLabel = cfg.get("fieldLabel", String.class);
    String fieldDesc = cfg.get("fieldDescription", String.class);
    String labelledBy = null;

    if (fieldLabel != null && fieldDesc != null) {
        labelledBy = fieldVM.get("labelId", String.class) + " " + fieldVM.get("descriptionId", String.class);
    } else if (fieldLabel != null) {
        labelledBy = fieldVM.get("labelId", String.class);
    } else if (fieldDesc != null) {
        labelledBy = fieldVM.get("descriptionId", String.class);
    }

    if (StringUtils.isNotBlank(labelledBy)) {
        attrs.add("labelledby", labelledBy);
        attrs.add("role", "group");
    }

    %><coral-fileupload <%= attrs.build() %>><%
        AttrBuilder dropzoneAttrs = new AttrBuilder(request, xssAPI);
        if (allowUpload) {
            dropzoneAttrs.add("coral-fileupload-dropzone", "");
        }
        dropzoneAttrs.addClass("cq-FileUpload-thumbnail");

        %><div <%= dropzoneAttrs.build() %>>
            <div class="cq-FileUpload-thumbnail-img" data-cq-fileupload-thumbnail-img><%
                if (assetExists) {
                    if (StringUtils.isNotEmpty(thumbnailSrc)) {
                        thumbnailTitle = StringUtils.isEmpty(thumbnailTitle) ? "" : thumbnailTitle;
                        thumbnailAlt = StringUtils.isEmpty(thumbnailAlt) ? thumbnailTitle : thumbnailAlt;

                        %><img src="<%= xssAPI.encodeForHTMLAttr(thumbnailSrc) %>"
                               alt="<%= xssAPI.encodeForHTMLAttr(thumbnailAlt) %>"
                               title="<%= xssAPI.encodeForHTMLAttr(thumbnailTitle) %>" /><%
                    } else if (StringUtils.isNotEmpty(thumbnailTitle)) {
                        %><p><%= xssAPI.encodeForHTML(thumbnailTitle) %></p><%
                    }
                }
            %></div><%

            String resourcePath = resource.getPath();

            ValueMap editBtnVM = new ValueMapDecorator(new HashMap<String, Object>());
            editBtnVM.put("granite:class", "cq-FileUpload-edit");
            editBtnVM.put("variant", "quiet");
            editBtnVM.put("text", "Edit");

            List<Resource> editBtnChildren = new ArrayList<Resource>();
            ValueMap editBtnChildrenVM = new ValueMapDecorator(new HashMap<String, Object>());
            editBtnChildrenVM.put("cq-fileupload-viewinadminuri", cfg.get("viewInAdminURI", "/assetdetails.html{+item}"));

            // Pass the asset path to the edit button
            if (isAssetFromDAM && StringUtils.isNotEmpty(resourceFilePath)) {
                editBtnChildrenVM.put("cq-fileupload-filereference", resourceFilePath);
            } else {
                editBtnVM.put("disabled", true);
            }

            editBtnChildren.add(new ValueMapResource(resourceResolver, resourcePath + "/granite:data", "nt:unstructured", editBtnChildrenVM));

            ValueMapResource editBtn = new ValueMapResource(resourceResolver, resourcePath, "granite/ui/components/coral/foundation/button", editBtnVM, editBtnChildren);

            %><sling:include resource="<%= editBtn %>"/><%

            %><button type="button" class="cq-FileUpload-clear" is="coral-button" variant="quiet" coral-fileupload-clear><%= xssAPI.encodeForHTML(i18n.getVar("Clear")) %></button>
              <button type="button" class="cq-FileUpload-picker" is="coral-button" variant="quiet" aria-haspopup="dialog"><%= xssAPI.encodeForHTML(i18n.getVar("Pick")) %></button>
              <div class="cq-FileUpload-thumbnail-dropHere"><%
                AttrBuilder iconAttrs = new AttrBuilder(request, xssAPI);
                iconAttrs.add("icon", cfg.get("icon", "image"));
                iconAttrs.addClass("cq-FileUpload-icon cq-FileUpload-picker");
                
                %><button type="button" is="coral-button" variant="quiet" aria-label="<%= xssAPI.encodeForHTML(i18n.get("Pick an asset")) %>" aria-haspopup="dialog" <%= iconAttrs.build() %>></button>
                <span class="cq-FileUpload-label"><%
                    if (allowDrop && allowUpload) {
                        %><%= xssAPI.encodeForHTML(i18n.get("Drop an asset here or", "Asset drop, part 1 of 3")) %><%
                        %> <a tabIndex="0" aria-label="<%= xssAPI.encodeForHTMLAttr(i18n.get("Browse for a file to upload")) %>" class="coral-Link cq-FileUpload-browse" coral-fileupload-select><%= xssAPI.encodeForHTML(i18n.get("browse", "Asset drop, part 2 of 3")) %></a><%
                        %> <%= xssAPI.encodeForHTML(i18n.get("for a file to upload.", "Asset drop, part 3 of 3")) %><%
                    } else if (allowDrop && !allowUpload) {
                        %><%= xssAPI.encodeForHTML(i18n.get("Drop an asset here.")) %><%
                    } else if (!allowDrop && allowUpload) {
                        %><%= xssAPI.encodeForHTML(i18n.get("Pick an asset or", "Asset drop, part 1 of 3")) %><%
                        %> <a tabIndex="0" aria-label="<%= xssAPI.encodeForHTMLAttr(i18n.get("Browse for a file to upload")) %>" class="coral-Link cq-FileUpload-browse" coral-fileupload-select><%= xssAPI.encodeForHTML(i18n.get("browse", "Asset drop, part 2 of 3")) %></a><%
                        %> <%= xssAPI.encodeForHTML(i18n.get("for a file to upload.", "Asset drop, part 3 of 3")) %><%
                    }
                %></span>
            </div>
        </div>
        <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(fileName) %>" data-cq-fileupload-parameter="filename" disabled="disabled"><%
        if (StringUtils.isNotEmpty(resourceFilePath)) {
    %><input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(fileReference) %>" data-cq-fileupload-parameter="filereference" value="<%= xssAPI.encodeForHTMLAttr(resourceFilePath) %>"><%
        } else { %>
        <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(fileReference) %>" data-cq-fileupload-parameter="filereference"><%
        }
        %><input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(fileDelete) %>" data-cq-fileupload-parameter="filedelete" disabled="disabled">
        <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(fileMoveFrom) %>" data-cq-fileupload-parameter="filemovefrom" value="<%= xssAPI.encodeForHTMLAttr(temporaryFilePath) %>" disabled="disabled">
        <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(lastModified) %>">
        <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(lastModifiedBy) %>">

    </coral-fileupload><%!

    /**
     * Transforms the given property value into an absolute path starting with "/"
     *
     * @param propertyStr
     * @return
     */
    String propertyToAbsolutePath(String propertyStr) {
        String path = "";

        if (StringUtils.isEmpty(propertyStr)) {
            return path;
        }

        if (propertyStr.startsWith("./")) {
            path += propertyStr.substring(1);
        } else if (propertyStr.startsWith("/")) {
            path += propertyStr;
        } else {
            path += "/" + propertyStr;
        }

        // Remove the trailing slash
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * Returns the thumbnail suffix based on the mime type of the image file.
     *
     * @param image The image component resource
     * @param relFilePath The relative path to the image file
     * @return ".img.svg" for SVG images, ".img.png" otherwise
     */
    String getThumbnailSuffix(Resource image, String relFilePath) {
        final String MIME_TYPE_SVG = "image/svg+xml";
        final String PN_MIME_TYPE = "jcr:mimeType";
        final String DEFAULT_THUMBNAIL_SUFFIX = ".img.png";
        final String SVG_THUMBNAIL_SUFFIX = ".img.svg";
        final String NN_JCR_CONTENT = "jcr:content";
        if (image != null) {
            Resource imageFile = image.getChild(relFilePath);
            if (imageFile != null) {
                Resource contentRes = imageFile.getChild(NN_JCR_CONTENT);
                if (contentRes != null) {
                    ValueMap props = contentRes.getValueMap();
                    String mimeType = props.get(PN_MIME_TYPE, String.class);
                    if (StringUtils.equals(mimeType, MIME_TYPE_SVG)) {
                        return SVG_THUMBNAIL_SUFFIX;
                    }
                }
            }
        }
        return DEFAULT_THUMBNAIL_SUFFIX;
    }

    /**
     * Checks if the property is allowed by the edit or the policy configuration.
     *
     * @param propName The property name
     * @param config The configuration
     * @param resource The resource
     * @param resolver The resource resolver
     * @return true if allowed, false otherwise
     */
    boolean isAllowed(String propName, Config config, Resource resource, ResourceResolver resolver) {
        boolean allowed = config.get(propName, true);
        if(resource != null) {
            ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
            ContentPolicy contentPolicy = policyManager.getPolicy(resource);

            if (contentPolicy != null) {
                boolean isAllowedInPolicy = contentPolicy.getProperties().get(propName) == null ||
                        Boolean.parseBoolean(contentPolicy.getProperties().get(propName).toString());
                allowed = allowed && isAllowedInPolicy;
            }
        }
        return allowed;
    }

%>
