angular.module('pid', ['ngRoute', 'ui.select', 'ngSanitize'])

// configure our routes
.config(function($routeProvider) {
    $routeProvider

    // route for the home page
        .when('/', {
            templateUrl : 'upload/upload.html',
            controller  : 'ImagemCtrl'
        })

        // route for the about page
        .when('/selectFunctions', {
            templateUrl : 'selectFunctions/selectFunctions.html',
            controller  : 'MainCtrl'
        });
});