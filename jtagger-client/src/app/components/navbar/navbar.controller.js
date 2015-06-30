'use strict';

angular.module('jtaggerClient').config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('library', {
            url: "/library",
            templateUrl: "app/components/library/library.html"
        })
        .state('home', {
            url: "/",
            templateUrl: "app/main/main.html"
        })
});