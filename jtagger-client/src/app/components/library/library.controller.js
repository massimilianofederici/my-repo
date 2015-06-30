'use strict';

angular.module('jtaggerClient').controller('LibraryCtrl', ['$scope', 'LibraryService',
    function($scope, libraryService) {
        $scope.actions = {};
        $scope.options = {};

        $scope.options.selectedFile = [];
        $scope.options.selectedMetaData = [];

        $scope.options.libraryGrid = {
            data: 'options.library.files',
            multiSelect: true,
            selectedItems: $scope.options.selectedFile,
            showSelectionCheckbox: true,
            showGroupPanel: true,
            groups: ['path'],
            columnDefs: [{
                field: 'fileName',
                displayName: 'File',
            }]
        };

        $scope.options.detailGrid = {
            data: 'options.details',
            selectedItems: $scope.options.selectedMetaData,
            multiSelect: false,
            enableCellEdit: true
        };

        $scope.actions.load = function() {
            $scope.options.loading = true;

            libraryService.getLibrary().then(function(data) {
                if (!data) {
                    $scope.actions.init();
                } else {
                    $scope.options.library = data;
                    onComplete();
                }
            }, onComplete);
        }

        $scope.actions.init = function() {
            var fullPath = 'C:/Users/Massimiliano/Downloads';
            libraryService.initialiseLibrary(fullPath).then($scope.actions.load);
        }

        $scope.actions.refresh = function() {
            $scope.options.loading = true;
            libraryService.refreshLibrary().then($scope.actions.load);
        }

        $scope.actions.update = function() {
            var dirtyFiles = [];
            angular.forEach($scope.options.library.files, function(file) {
                if (file.dirty) {
                    dirtyFiles.push({
                        fileName: file.fileName,
                        tags: file.tags
                    });
                }
            })
            libraryService.updateMetaData(dirtyFiles);
        }

        $scope.$watchCollection('options.selectedFile', function(newValue) {
            var details = [];
            var MULTIPLE_VALUES = "<MULTIPLE_VALUES>";
            var getEntry = function(key) {
                for (var i = details.length - 1; i >= 0; i--) {
                    var element = details[i];
                    if (element.key === key) {
                        return element;
                    }
                };
                return null;
            }

            angular.forEach(newValue, function(value) {
                var entries = value.tags;
                angular.forEach(entries, function(entry) {
                    // details.push(entry);
                    var existingEntry = getEntry(entry.key);
                    if (!existingEntry) {
                        details.push({
                            key: entry.key,
                            value: entry.value
                        });
                    } else {
                        if (existingEntry.value != entry.value) {
                            existingEntry.value = MULTIPLE_VALUES;
                        }
                    }
                })
            });
            $scope.options.details = details;
        })

        $scope.$on('ngGridEventEndCellEdit', function(a) {
            var selectedMetaData = $scope.options.selectedMetaData[0];
            var key = selectedMetaData.key;
            var value = selectedMetaData.value;
            var getEntry = function(key, entries) {
                for (var i = entries.length - 1; i >= 0; i--) {
                    var element = entries[i];
                    if (element.key === key) {
                        return element;
                    }
                };
                return null;
            }
            angular.forEach($scope.options.selectedFile, function(item) {
                getEntry(key, item.tags).value = value;
                item.dirty = true;
            });
        });


        var onComplete = function() {
            $scope.options.loading = false;
        };

        $scope.actions.load();
    }
]);