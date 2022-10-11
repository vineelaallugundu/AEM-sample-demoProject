var MimeTypes = Packages.org.apache.jackrabbit.vault.util.MimeTypes;
var DamConstants = Packages.com.day.cq.dam.api.DamConstants;
use(function () {
    if (this.imagePath != null && !DamConstants.THUMBNAIL_MIMETYPE.equalsIgnoreCase(MimeTypes.getMimeType(this.imagePath))) {
      return this.imagePath + ".transform/" + this.width + "/image.jpg";
   }
    return this.imagePath;
});