angular.module('pid', ['ngRoute'])

    .controller('MainCtrl', function ($scope, $http) {

    })

// configure our routes
.config(function($routeProvider) {
    $routeProvider

    // route for the home page
        .when('/', {
            templateUrl : 'upload/upload.html',
            controller  : 'ImagemCtrl'
        })

        // route for the about page
        .when('/about', {
            templateUrl : 'selectFunctions/selectFunctions.html',
            controller  : 'MainCtrl'
        });
});