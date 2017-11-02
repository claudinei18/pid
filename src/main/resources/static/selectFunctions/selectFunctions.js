angular.module('pid')
    .controller('MainCtrl', function ($scope, $rootScope, $http) {
        $scope.url = "http://localhost:5000/imagens/" + $rootScope.codigoImagem + "/" + $rootScope.nomeImagem;

        $scope.functionsSelected = [];
        $scope.functionsSelected.push('');


        $scope.functions = [
            {nome: "Passa baixa"},
            {nome: "Passa alta"},
            {nome: "Logaritmico", c: "1235"}
        ];


        $scope.changeFunction = function(index) {
            console.log($scope.functionsSelected);
            if (index == $scope.functionsSelected.length -1) {
                $scope.functionsSelected.push('');
            }
        };

    })