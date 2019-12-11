// ParsleyConfig definition if not already set
window.ParsleyConfig = window.ParsleyConfig || {};
window.ParsleyConfig.i18n = window.ParsleyConfig.i18n || {};

// Define then the messages
window.ParsleyConfig.i18n.de = $.extend(window.ParsleyConfig.i18n.de || {}, {
  defaultMessage: "Die Eingabe scheint nicht korrekt zu sein.",
  type: {
    email:        "Die Eingabe muss eine gültige E-Mail-Adresse sein.",
    url:          "Die Eingabe muss eine gültige URL sein.",
    number:       "Die Eingabe muss eine Zahl sein.",
    integer:      "Die Eingabe muss eine Zahl sein.",
    digits:       "Die Eingabe darf nur Ziffern enthalten.",
    alphanum:     "Die Eingabe muss alphanumerisch sein."
  },
  notblank:       "Die Eingabe darf nicht leer sein.",
  required:       "Eingabe erforderlich",
  pattern:        "Die Eingabe scheint ungültig zu sein.",
  min:            "Die Eingabe muss größer oder gleich %s sein.",
  max:            "Die Eingabe muss kleiner oder gleich %s sein.",
  range:          "Die Eingabe muss zwischen %s und %s liegen.",
  minlength:      "Die Eingabe ist zu kurz. Es müssen mindestens %s Zeichen eingegeben werden.",
  maxlength:      "Die Eingabe ist zu lang. Es dürfen höchstens %s Zeichen eingegeben werden.",
  length:         "Die Länge der Eingabe ist ungültig. Es müssen zwischen %s und %s Zeichen eingegeben werden.",
  equalto:        "Dieses Feld muss dem anderen entsprechen."
});

//Multi upload text elements
window.fileupload = window.fileupload || {};
window.fileupload.de = {};
window.fileupload.de.messages =  {
  maxFileSize: "Datei ist zu groß",
  minFileSize: "Datei ist zu klein",
  acceptFileTypes: "Dateityp nicht erlaubt",
  maxNumberOfFiles: "Maximale Anzahl an Dateien überschritten",
  uploadedBytes: "Dateigröße übersteigt Limit",
  emptyResult: "Die Datei ist leer",
  deleteText: "Löschen",
  uploadedText: "Hochgeladen"
};

//Multi upload text elements
window.fileupload = window.fileupload || {};
window.fileupload.en = {};
window.fileupload.en.messages =  {
  maxFileSize: "File size too big",
  minFileSize: "File size too small",
  acceptFileTypes: "File type not allowed",
  maxNumberOfFiles: "Too much files",
  uploadedBytes: "File size too big",
  emptyResult: "File is empty",
  deleteText: "Delete",
  uploadedText: "Uploaded"
};

// If file is loaded after Parsley main file, auto-load locale
if ('undefined' !== typeof window.ParsleyValidator){
  window.ParsleyValidator.addCatalog('de', window.ParsleyConfig.i18n.de, true);
}