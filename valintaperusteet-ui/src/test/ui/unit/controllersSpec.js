'use strict';

describe('JarjestyskriteeriController', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('valintaperusteet'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        var routeParams = {"valinnanvaiheOid": "oid1",
                            "valintatapajonoOid": "oid2",
                            "jarjestyskriteeriOid": "oid3"};

        var laskentakaavat = [{"id": "1"}];
        var jarjestyskriteeri ={"nimi": "jk"};
        $httpBackend.when('GET', 'resources/jarjestyskriteeri/oid3').respond(jarjestyskriteeri);
        $httpBackend.when('GET', 'resources/laskentakaava').respond(laskentakaavat);
        $httpBackend.when('GET', 'resources/laskentakaava/1').respond();

        ctrl = $controller(JarjestyskriteeriController, {$scope: scope, $routeParams: routeParams});
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should have valinnanvaiheOid', function() {
        $httpBackend.flush();
        expect(scope.valinnanvaiheOid).toBe("oid1");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.valintatapajonoOid).toBe("oid2");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.model.laskentakaavat[0].id).toBe("1");
    });

    it('should have jarjestyskriteeri', function() {
        $httpBackend.flush();
        expect(scope.model.jarjestyskriteeri.nimi).toBe("jk");
    });

    it('should send put', function() {
        $httpBackend.flush();
        $httpBackend.expectPUT('resources/valintatapajono/oid2/jarjestyskriteeri').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

});

describe('HakukohdeValintatapajonoController', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('valintaperusteet'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        var routeParams = {"valinnanvaiheOid": "oid1",
                            "valintatapajonoOid": "oid2",
                            "hakukohdeOid": "oid3"};

        var jarjestyskriteerit = [{"oid": "1", "laskentakaava_id": "oid4"}];
        var jono ={"nimi": "jono"};
        $httpBackend.when('GET', 'resources/valintatapajono/oid2').respond(jono);
        $httpBackend.when('GET', 'resources/valintatapajono/oid2/jarjestyskriteeri').respond(jarjestyskriteerit);
        $httpBackend.when('GET', 'resources/laskentakaava/oid4').respond();

        ctrl = $controller(HakukohdeValintatapajonoController, {$scope: scope, $routeParams: routeParams});
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should have valinnanvaiheOid', function() {
        $httpBackend.flush();
        expect(scope.hakukohdeOid).toBe("oid3");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.valinnanvaiheOid).toBe("oid1");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.model.jarjestyskriteerit[0].oid).toBe("1");
    });

    it('should have jarjestyskriteeri', function() {
        $httpBackend.flush();
        expect(scope.model.valintatapajono.nimi).toBe("jono");
    });

    it('should send put', function() {
        $httpBackend.flush();
        $httpBackend.expectPUT('resources/valinnanvaihe/oid1/valintatapajono').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

    it('should send put', function() {
        $httpBackend.flush();
        scope.model.valintatapajono.oid = "oid";

        $httpBackend.expectPOST('resources/valintatapajono/oid').respond(201, '')
        $httpBackend.expectPOST('resources/jarjestyskriteeri/1').respond(201, '')
        $httpBackend.expectPOST('resources/jarjestyskriteeri/jarjesta').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

});

describe('ValintaryhmaValintatapajonoController', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('valintaperusteet'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        var routeParams = {"valinnanvaiheOid": "oid1",
                            "valintatapajonoOid": "oid2",
                            "id": "oid3"};

        var jarjestyskriteerit = [{"oid": "1", "laskentakaava_id": "oid4"}];
        var jono ={"nimi": "jono"};
        $httpBackend.when('GET', 'resources/valintatapajono/oid2').respond(jono);
        $httpBackend.when('GET', 'resources/valintatapajono/oid2/jarjestyskriteeri').respond(jarjestyskriteerit);
        $httpBackend.when('GET', 'resources/laskentakaava/oid4').respond();

        ctrl = $controller(ValintaryhmaValintatapajonoController, {$scope: scope, $routeParams: routeParams});
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should have valinnanvaiheOid', function() {
        $httpBackend.flush();
        expect(scope.valintaryhmaOid).toBe("oid3");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.valinnanvaiheOid).toBe("oid1");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.model.jarjestyskriteerit[0].oid).toBe("1");
    });

    it('should have jarjestyskriteeri', function() {
        $httpBackend.flush();
        expect(scope.model.valintatapajono.nimi).toBe("jono");
    });

    it('should send put', function() {
        $httpBackend.flush();
        $httpBackend.expectPUT('resources/valinnanvaihe/oid1/valintatapajono').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

    it('should send put', function() {
        $httpBackend.flush();
        scope.model.valintatapajono.oid = "oid";

        $httpBackend.expectPOST('resources/valintatapajono/oid').respond(201, '')
        $httpBackend.expectPOST('resources/jarjestyskriteeri/1').respond(201, '')
        $httpBackend.expectPOST('resources/jarjestyskriteeri/jarjesta').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

});

describe('valintaryhmaValinnanvaiheController', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('valintaperusteet'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        var routeParams = {"valinnanvaiheOid": "oid1",
                            "id": "oid3"};

        var valintatapajonot = [{"oid": "1"}];
        var valinnanvaihe ={"nimi": "jono"};
//        $httpBackend.when('GET', 'resources/valintatapajono/oid2').respond(jono);
        $httpBackend.when('GET', 'resources/valinnanvaihe/oid1/valintatapajono').respond(valintatapajonot);
        $httpBackend.when('GET', 'resources/valinnanvaihe/oid1').respond(valinnanvaihe);
        $httpBackend.when('GET', 'resources/valintaryhma?paataso=true').respond();
        $httpBackend.when('GET', 'resources/hakukohde?paataso=true').respond();

        ctrl = $controller(valintaryhmaValinnanvaiheController, {$scope: scope, $routeParams: routeParams});
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should have valinnanvaiheOid', function() {
        $httpBackend.flush();
        expect(scope.valintaryhmaOid).toBe("oid3");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.ValintaryhmaValinnanvaiheOid).toBe("oid1");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.model.valintatapajonot[0].oid).toBe("1");
    });

    it('should have jarjestyskriteeri', function() {
        $httpBackend.flush();
        expect(scope.model.valinnanvaihe.nimi).toBe("jono");
    });

    it('should send put', function() {
        $httpBackend.flush();
        $httpBackend.expectPUT('resources/valintaryhma/oid3/valinnanvaihe').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

    it('should send post', function() {
        $httpBackend.flush();
        scope.model.valinnanvaihe.oid = "oid";

        $httpBackend.expectPOST('resources/valinnanvaihe/oid').respond(201, '')
        $httpBackend.expectPOST('resources/valintatapajono/jarjesta').respond(201, '')
        $httpBackend.expectPOST('resources/valintatapajono/1').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

});

describe('ValinnanVaiheController', function(){
    var scope, ctrl, $httpBackend;

    beforeEach(module('valintaperusteet'));

    beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        var routeParams = {"valinnanvaiheOid": "oid1",
                            "hakukohdeOid": "oid3"};

        var valintatapajonot = [{"oid": "1"}];
        var valinnanvaihe = {"nimi": "valinnanvaihe"};
        $httpBackend.when('GET', 'resources/valinnanvaihe/oid1/valintatapajono').respond(valintatapajonot);
        $httpBackend.when('GET', 'resources/valinnanvaihe/oid1/kuuluuSijoitteluun').respond();
        $httpBackend.when('GET', 'resources/valinnanvaihe/oid1').respond(valinnanvaihe);

        ctrl = $controller(ValinnanVaiheController, {$scope: scope, $routeParams: routeParams});
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should have valinnanvaiheOid', function() {
        $httpBackend.flush();
        expect(scope.hakukohdeOid).toBe("oid3");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.valinnanvaiheOid).toBe("oid1");
    });

    it('should have valintatpajonoOid', function() {
        $httpBackend.flush();
        expect(scope.model.valintatapajonot[0].oid).toBe("1");
    });

    it('should have jarjestyskriteeri', function() {
        $httpBackend.flush();
        expect(scope.model.valinnanvaihe.nimi).toBe("valinnanvaihe");
    });

    it('should send put', function() {
        $httpBackend.flush();
        $httpBackend.expectPUT('resources/hakukohde/oid3/valinnanvaihe').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

    it('should send post', function() {
        $httpBackend.flush();
        scope.model.valinnanvaihe.oid = "oid";

        $httpBackend.expectPOST('resources/valinnanvaihe/oid').respond(201, '')
        $httpBackend.expectPOST('resources/valintatapajono/jarjesta').respond(201, '')
        $httpBackend.expectPOST('resources/valintatapajono/1').respond(201, '')
        scope.submit();
        $httpBackend.flush();
    });

});