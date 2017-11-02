angular.module('pid')

    .controller('ImagemCtrl', function ($scope, $rootScope, $http, $location) {





        $scope.uploadImagem = function () {
            $scope.desativado = true;
            console.log("Uploading")

            $rootScope.nomeImagem = $scope.arquivo;
            console.log($scope.arquivo);

            // Esse formulario necessita ser feito dessa forma para
            // pegar o arquivo upado juntamente com os dados do chamado
            var formData = new FormData();
            //Evita que o arquivo esteja vazio ou não tenha sido selecionado
            if ($('input[type=file]')[0].files[0] != undefined) {
                formData.append('arquivo', $('input[type=file]')[0].files[0]);
                $rootScope.codigoImagem = makeid();
                formData.append('codigo', $rootScope.codigoImagem);
            }
            /* Efetua o post de abertura do chamado */
            console.log(formData)
            $http.post('/rest/imagem', formData, {
                transformRequest: function (data) {
                    return data;
                },
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                console.log(response)
                var path = response.data.path;
                $rootScope.path = path;
                $rootScope.nomeImagem = response.data.nomeImagem;
                alert('You\'re about to go back to page 1...');
                $location.path('/selectFunctions');
            });
        };

        function makeid() {
            var text = "";
            var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            for (var i = 0; i < 5; i++)
                text += possible.charAt(Math.floor(Math.random() * possible.length));

            return text;
        }
    })