app.factory('ValintaryhmaValinnanvaiheModel', function(Valinnanvaihe, Valintatapajono, ValinnanvaiheValintatapajono, NewValintaryhmaValinnanvaihe, ValintatapajonoJarjesta) {
    
    var model = new function() {
        
        this.valinnanvaihe = {};
        this.valintatapajonot = [];

        this.refresh = function (oid) {
            if(!oid) {
                model.valinnanvaihe = {};
                model.valintatapajonot = [];
            } else {

                Valinnanvaihe.get({oid: oid}, function(result) {
                    model.valinnanvaihe = result;
                });

                ValinnanvaiheValintatapajono.get({parentOid: oid}, function(result) {
                    model.valintatapajonot = result;
                });
            }
        };
        this.refreshIfNeeded = function(oid) {
            if(oid !== model.valinnanvaihe.oid) {
                model.refresh(oid)
            }
        };
        this.persistValintaryhmaValinnanvaihe = function(parentValintaryhmaOid, valinnanvaiheet) {
            if(model.valinnanvaihe.oid) {
                
                Valinnanvaihe.post(model.valinnanvaihe, function(result) {
                    var i;
                    //update valinnanvaihe in ValintaryhmaModel
                    for(i in valinnanvaiheet) {
                        if(result.oid === valinnanvaiheet[i].oid) {
                            valinnanvaiheet[i] = result;
                        }
                    }
                });

                ValintatapajonoJarjesta.post(getValintatapajonoOids(), function(result) {});

                model.valintatapajonot.forEach(function(element, index, array){
                    Valintatapajono.post({oid: model.valintatapajonot[index].oid}, element, function(result) {
//                        model.valintatapajonot[index] = result;
                    });
                });
                
            } else {
                var valinnanvaihe = {
                    nimi: model.valinnanvaihe.nimi,
                    kuvaus: model.valinnanvaihe.kuvaus,
                    aktiivinen: true,
                    valinnanVaiheTyyppi: "TAVALLINEN"
                }
                NewValintaryhmaValinnanvaihe.put({valintaryhmaOid: parentValintaryhmaOid}, valinnanvaihe, function(result){
                    model.valinnanvaihe = result;
                    valinnanvaiheet.push(result);
                });
            }
        };
        this.remove = function(jono) {
            Valintatapajono.delete({oid: jono.oid}, function(result) {    
                model.refresh(model.valinnanvaihe.oid);
            })
        }

        function getValintatapajonoOids() {
            var oids = [];
            for (var i = 0 ; i < model.valintatapajonot.length ; ++i) {
                oids.push(model.valintatapajonot[i].oid);
            }
            return oids;
        }

    }
    return model;

});

function valintaryhmaValinnanvaiheController($scope, $location, $routeParams, ValintaryhmaValinnanvaiheModel, ValintaryhmaModel) {
    $scope.valintaryhmaOid = $routeParams.id;
    $scope.ValintaryhmaValinnanvaiheOid = $routeParams.valinnanvaiheOid;
    $scope.model = ValintaryhmaValinnanvaiheModel;
    $scope.model.refreshIfNeeded($scope.ValintaryhmaValinnanvaiheOid);

    $scope.submit = function() {
        $scope.model.persistValintaryhmaValinnanvaihe($scope.valintaryhmaOid, ValintaryhmaModel.valinnanvaiheet);
    }

    $scope.cancel = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid);
    }

    $scope.addJono = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valinnanvaihe/" + $scope.model.valinnanvaihe.oid + "/valintatapajono/");
    }

    $scope.modifyJono = function(oid) {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valinnanvaihe/" + $scope.model.valinnanvaihe.oid + "/valintatapajono/" + oid);
    }

}



app.factory('ValintaryhmaValintakoeValinnanvaiheModel', function(Valinnanvaihe, ValinnanvaiheValintakoe, NewValintaryhmaValinnanvaihe, Valintakoe) {
    var model = new function() {
        
        this.valintakoevalinnanvaihe = {};
        this.valintakokeet = [];

        this.refresh = function(oid) {
            if(!oid) {
                model.valintakoevalinnanvaihe = {};
                model.valintakokeet = [];
            } else {
                Valinnanvaihe.get({oid: oid}, function(result) {
                    model.valintakoevalinnanvaihe = result;
                });

                ValinnanvaiheValintakoe.get({valinnanvaiheOid: oid}, {}, function(result) {
                    model.valintakokeet = result;
                });

            }
        }

        this.persist = function(parentValintaryhmaOid, valinnanvaiheet) {
            if(model.valintakoevalinnanvaihe.oid) {
                Valinnanvaihe.post(model.valintakoevalinnanvaihe, function(result) {
                    var i;
                    for(i in valinnanvaiheet) {
                        if(result.oid === valinnanvaiheet[i].oid) {
                            valinnanvaiheet[i] = result;
                        }
                    }
                });
            } else {
               
                var valintakoevalinnanvaihe = {
                    nimi: model.valintakoevalinnanvaihe.nimi,
                    kuvaus: model.valintakoevalinnanvaihe.kuvaus,
                    aktiivinen: true,
                    valinnanVaiheTyyppi: "VALINTAKOE"
                }

                NewValintaryhmaValinnanvaihe.put({valintaryhmaOid: parentValintaryhmaOid}, valintakoevalinnanvaihe, function(result){
                    model.valintakoevalinnanvaihe = result;
                    valinnanvaiheet.push(result);
                });
            }
        }

        this.removeValintakoe = function(valintakoe) {
            Valintakoe.remove({valintakoeOid: valintakoe.oid}, function(result){
                for(i in model.valintakokeet) {
                    if(valintakoe.oid === model.valintakokeet[i].oid) {
                        model.valintakokeet.splice(i,1);
                    }
                }
            });
        }

    }

    return model;
});

function ValintaryhmaValintakoeValinnanvaiheController($scope, $location, $routeParams, ValintaryhmaValintakoeValinnanvaiheModel, ValintaryhmaModel) {
    $scope.valintaryhmaOid = $routeParams.id;
    $scope.ValintaryhmaValintakoeValinnanvaiheOid = $routeParams.valintakoevalinnanvaiheOid;
    $scope.model = ValintaryhmaValintakoeValinnanvaiheModel;
    $scope.model.refresh($scope.ValintaryhmaValintakoeValinnanvaiheOid);
    

    $scope.submit = function() {
        $scope.model.persist($scope.valintaryhmaOid, ValintaryhmaModel.valinnanvaiheet);
    }

    $scope.modifyvalintakoe = function(valintakoeOid) {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valintakoevalinnanvaihe/" + $scope.model.valintakoevalinnanvaihe.oid + "/valintakoe/" + valintakoeOid);
    }

    $scope.addValintakoe = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid + "/valintakoevalinnanvaihe/" + $scope.model.valintakoevalinnanvaihe.oid + "/valintakoe/");
    }

    $scope.cancel = function() {
        $location.path("/valintaryhma/" + $scope.valintaryhmaOid);
    }

}
