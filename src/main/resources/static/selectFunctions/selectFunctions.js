angular.module('pid')
    .controller('MainCtrl', function ($scope, $rootScope, $http) {
        $scope.url = "http://localhost:5000/imagens/" + $rootScope.codigoImagem + "/" + $rootScope.nomeImagem;
        $scope.teste = function () {
            console.log($rootScope.nomeImagem);
        }

    })