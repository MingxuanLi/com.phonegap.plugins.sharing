/**
 * Created by mingxuanli on 9/08/2015.
 */

/*global cordova, module*/

module.exports = {
    get_content: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SharingPlugin", "get_content", []);
    }
};