angular.module('pid', ['ngRoute', 'ui.select', 'ngSanitize', 'ui.bootstrap', 'dialogs', 'colorpicker.module'])



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
})

.controller('appCtrl', function ($scope, $location, $anchorScroll) {
    $scope.scrollTo = function(id) {
      $location.hash(id);
      $anchorScroll();
   }
});

