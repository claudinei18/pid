angular.module('pid')
    .controller('MainCtrl', function ($scope, $rootScope, $http, $dialogs, $timeout) {

        $scope.url = "http://localhost:5000/imagens/" + $rootScope.codigoImagem + "/" + $rootScope.nomeImagem;

        $scope.showHistograma = false;
        $scope.showHistogramaEqualizado = true;

        $scope.imagens = [];

        $scope.functionsSelected = [];
        $scope.functionsSelected.push('');
        $scope.gaussOptions = ["1", "2", "3", "4", "5", "6", "7", "8"];


        /*ate agora:
            Negativo(nome do arquivo)
        HistogramEq(nome do arquivo)
        Laplace(nome do arquivo)
        logaritmica(nome do arquivo, c-sem limitar da pra alterar)
        potencia(nome do arquivo, c, gama)
        tresholding(nome do arquivo, limiar, intensid. cor1, intensid. cor2)
        somadeiamgem(nome arquivo1, nome arquivo2
        GenericMask(nome do arquivo, dimensoes da mascara, tag, valores da mascara de 1,1 ate n,n)*/

        /*new GenericMask().filter(params);
        new HistogramEqualization().filter(params);
        new DigitalNegative().filter(params);
        new Laplace().filter(params);
        new Median().filter(params);
        new Min().filter(params);
        new Max().filter(params);

        params.set(1, "35");
        new Logarithmic().filter(params);

        params.set(1, "25");
        params.set(2, "0.4");
        new Potency().filter(params);

        params.set(1, "160");
        params.set(2, "255");
        params.set(3, "0");
        new Tresholding().filter(params);

        params.set(1, "laplace_mask 3x3_img.jpg");
        new SumImage().filter(params);
        new SubtractImage().filter(params);

        params.set(1, "8");
        new Gaussian().filter(params);*/

        $scope.functions = [
            {nome: "Negativo"},
            {nome: "Equalização de Histograma"},
            {nome: "Laplace"},
            {nome: "Mediana"},
            {nome: "MIN"},
            {nome: "MAX"},
            {nome: "Logarítmica", c: 1},
            {nome: "Potência", c: 1, gama: 1},
            {nome: "Tresholding", limiar: 1},
            {nome: "Soma", imagem2: ""},
            {nome: "Subtração", imagem2: ""},
            {nome: "Gaussiano", gauss: 2},
            {nome: "Limiarização Global"},
            {nome: "Máscara Genérica", dimensoesDaMascara: "", valoresDaMascara: ""}

        ];

        $scope.matrix = [[0]];
        $scope.somasEsubtracoes = [];

        $scope.add = function () {
            $scope.addColumn()
            $scope.addRow()
        }

        $scope.addColumn = function() {
            $scope.matrix.forEach(function(row) {
                row.push(0);
            });
        };

        $scope.addRow = function() {
            var columnCount = $scope.matrix[0].length;
            var newRow = [];
            for (var i = 0; i < columnCount; i++) {
                newRow.push(0);
            }
            $scope.matrix.push(newRow);
        };

        $scope.deleteRow = function(idx) {
            if (idx >= 0 && idx < $scope.matrix.length) {
                $scope.matrix.splice(idx, 1);
            }
        };

        $scope.deleteColumn = function(idx) {
            if (idx >= 0 && idx < $scope.matrix[0].length) {
                $scope.matrix.forEach(function(row) {
                    row.splice(idx, 1);
                });
            }
        };


        $scope.runTransformacoes = function () {

            var index = $scope.functionsSelected.map(function(d) { return d['nome']; }).indexOf('Máscara Genérica')
            if(index >= 0){
                $scope.functionsSelected[index].valoresDaMascara = $scope.matrix;
                $scope.functionsSelected[index].dimensoesDaMascara = $scope.matrix[0].length;
            }

            var body = {
                codeImagemOriginal: $rootScope.codigoImagem,
                nomeImagem: $rootScope.nomeImagem,
                funcoes: $scope.functionsSelected
            };
            $scope.runFuntions(body);


        }


        $scope.random = function () {
            return (new Date()).toString();
        }

        $scope.runFuntions = function (body) {
            waitingDialog.show('Running ... Please wait');
            $http.post('/rest/funcoes', body).then(function (response) {
                if (response.data) {
                    $scope.somasEsubtracoes = []
                    $scope.msg = "Post Data Submitted Successfully!";
                    console.log(response);
                    console.log(response.data)
                    $scope.imagens = response.data;
                    $scope.histogramaImagemOriginal = JSON.parse(response.data[response.data.length - 1].histogramaImagemOriginal);
                    console.log($scope.imagens);

                    $scope.showHistograma = true;
                    $scope.desenharGrafico();
                    for (var j = 0; j < $scope.imagens.length; j++) {
                        console.log($scope.imagens[j].nomeTransformacao)
                        var nome = $scope.imagens[j].nomeTransformacao
                        if (nome == 'Equalização de Histrograma') {
                            console.log("Desenhando")
                            console.log($scope.imagens[j])
                            $scope.urlImagemHistograma = $scope.imagens[j].url;
                            $scope.showHistogramaEqualizado = true;
                            $scope.urlImagemOriginalTonsDeCinza = $scope.url + "_grayscale";
                            $scope.desenharGraficoOutroHistograma(JSON.parse($scope.imagens[j].histogramaEqualizado));
                        }else if(nome == 'Soma' || nome == 'Subtração'){
                            console.log($scope.imagens[j])
                            $scope.somasEsubtracoes.push($scope.imagens[j]);
                            $scope.urlImagem1 = $scope.imagens[j].urlImagem1;
                            $scope.urlImagem2 = $scope.imagens[j].urlImagem2;
                            $scope.urlImagemResultante = $scope.imagens[j].url;
                            $scope.funcao = nome;
                            $scope.soma = true;
                        }
                    }

                }
                waitingDialog.hide();
            }, function (response) {
                waitingDialog.hide();
                $scope.msg = "Service not Exists";
                alert($scope.msg);
                $scope.statusval = response.status;
                $scope.statustext = response.statusText;
                $scope.headers = response.headers();
            });

        }

        $scope.changeFunction = function (index) {
            console.log($scope.functionsSelected);
            if (index == $scope.functionsSelected.length - 1) {
                $scope.functionsSelected.push('');
            }
        };

        $scope.desenharGrafico = function () {
            var ctx = document.getElementById("myChart").getContext('2d');
            var labels = [];
            for (var i = 0; i < 256; i++) {
                labels[i] = "" + i;
            }
            var backgroundColor = [];
            for (var i = 0; i < 256; i++) {
                backgroundColor[i] = 'rgba(' + i + ',' + i + ',' + i + ', 0.2)'
            }
            var borderColor = [];
            for (var i = 0; i < 256; i++) {
                borderColor[i] = 'rgba(' + i + ',' + i + ',' + i + ', 1)'
            }
            console.log(backgroundColor)
            console.log(borderColor)
            var myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: borderColor,
                    datasets: [{
                        label: '# of Pixels',
                        data: $scope.histogramaImagemOriginal,
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });

            //for (var i = 0; i < 256; i++) {
            //    myChart.data.datasets[i].fillColor = 'rgba(' + i + ',' + i + ',' + i + ', 0.2)';
            //}


        }

        $scope.desenharGraficoOutroHistograma = function (hist) {
            var ctx = document.getElementById("equalizacao").getContext('2d');
            var labels = [];
            for (var i = 0; i < 256; i++) {
                labels[i] = "" + i;
            }

            var myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '# of Pixels',
                        data: hist,
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });
        }


        $scope.uploadImagem = function (i) {
            waitingDialog.show('Uploading...');
            $scope.desativado = true;
            console.log("Uploading")

            var nomeImagem = $scope.arquivo;
            console.log($scope.arquivo);

            // Esse formulario necessita ser feito dessa forma para
            // pegar o arquivo upado juntamente com os dados do chamado
            var formData = new FormData();
            //Evita que o arquivo esteja vazio ou não tenha sido selecionado
            if ($('input[type=file]')[0].files[0] != undefined) {
                formData.append('arquivo', $('input[type=file]')[0].files[0]);
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
                $rootScope.pathImagemSomada = path;
                $rootScope.nomeImagemSomada = response.data.nomeImagem;

                $scope.functionsSelected[i].imagem2 = $rootScope.pathImagemSomada;
                waitingDialog.hide();

            });
            waitingDialog.hide();
        };

        $scope.compararHistogramas = function () {
            for (var i = 0; i < $scope.functionsSelected.length; i++) {
                if ($scope.functionsSelected[i].hasOwnProperty('nome') &&
                    $scope.functionsSelected[i].nome == 'Equalização de Histograma') {
                    return true;
                }
            }
            return false

        }

        $scope.openModal = function (imagem) {
            $scope.imagemClicada = imagem;
            $("#myModal").modal();

        }
    })
