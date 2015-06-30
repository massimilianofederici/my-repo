'use strict';

angular.module('jtaggerClient').
config(["RestangularProvider",
    function(RestangularProvider) {
        RestangularProvider.setBaseUrl('http://localhost:8080');
    }
]).
service('LibraryService', ['Restangular', '$q',
    function(Restangular, $q) {

        var libraryResource = Restangular.all('library');
        var service = this;
        this.getLibrary = function() {
            return libraryResource.get("");
        }

        this.initialiseLibrary = function(fullPath) {
            return libraryResource.customPOST(fullPath, 'initialise');
        }

        this.refreshLibrary = function() {
            return libraryResource.customPOST('', 'refresh');
        }

        this.updateMetaData = function(dirtyFiles) {
            return libraryResource.customPOST(dirtyFiles, 'update');
        }

    }
]);