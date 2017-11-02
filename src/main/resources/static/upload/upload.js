angular.module('pid')

    .controller('ImagemCtrl', function ($scope, $http) {

        $scope.uploadImagem = function () {
            $scope.desativado = true;
            console.log("Uploading")
            // Esse formulario necessita ser feito dessa forma para
            // pegar o arquivo upado juntamente com os dados do chamado
            var formData = new FormData();
            //Evita que o arquivo esteja vazio ou não tenha sido selecionado
            if ($('input[type=file]')[0].files[0] != undefined) {
                formData.append('arquivo', $('input[type=file]')[0].files[0]);
                formData.append('codigo', makeid())
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