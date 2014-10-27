/**
 * Phonegap ClipboardManager plugin
 * Omer Saatcioglu 2011
 * Guillaume Charhon - Smart Mobile Software 2011
 * Jacob Robbins - Phonegap 2.0 port 2013
 * Guillaume Charhon - Smart Mobile Software - Phonegap 3.0 port - 2013
 */

var ImgDownloader = {};

ImgDownloader.download = function(url, success, fail) {
    return cordova.exec(success, fail, "ImgDownloader", "download", [url]);
};

module.exports = ImgDownloader;
