describe("Retrospective Checkin", function() {

    var viewModel;
    var mockSuccessfulPatientRetrieval = function() {
        spyOn($, "ajax").andCallFake(function(params) {
            params.success(
                $.parseJSON('[' +
                    '{"patientId":"12","gender":"M","age":"47","birthdate":"09-Aug-1965","birthdateEstimated":"false",' +
                    '"preferredName":{"givenName":"Alberto","middleName":"","familyName":"Dummont","familyName2":"","fullName":"Alberto Dummont"},' +
                    '"primaryIdentifiers":[{"identifier":"TT309R"}]}]'));
        });
    }

    var locations = [{label: "Location 1", value: 1}, {label: "Location 2", value: 2}];
    var paymentReasons = [{label: "Reason 1", value: 1}, {label: "Reason 2", value: 2}];
    var paymentAmounts = [{label: "10 Moneys", value: 10}, {label: "20 Moneys", value: 20}];

    beforeEach(function() {
        viewModel = RetrospectiveCheckinViewModel(locations, paymentReasons, paymentAmounts);
    })

    it("should find a patient", function() {
        mockSuccessfulPatientRetrieval();
        viewModel.patientIdentifier("TT309R");
        expect(viewModel.patientName()).toBe("Alberto Dummont");
    });


    it("should not find a patient", function() {
        spyOn($, "ajax").andCallFake(function(params) {
            params.success($.parseJSON("[]"));
        });

        viewModel.patientIdentifier("TT012345");
        expect(viewModel.patientName()).toBe(undefined);
    });

    it("should validate checkin information", function() {
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        mockSuccessfulPatientRetrieval();
        viewModel.patientIdentifier("TT309R");
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        var location = viewModel.locations().options()[0];
        viewModel.locations().selectOption(location);
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        viewModel.checkinDay("23");
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        viewModel.checkinMonth("10");
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        viewModel.checkinYear("2012");
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        viewModel.checkinHour("13");
        expect(viewModel.checkinInfoIsValid()).toBe(false);

        viewModel.checkinMinutes("04");
        expect(viewModel.checkinInfoIsValid()).toBe(true);
    });

    it("should validate payment information", function() {
        expect(viewModel.paymentInfoIsValid()).toBe(false);

        var paymentReason = viewModel.paymentReasons().options()[0];
        viewModel.paymentReasons().selectOption(paymentReason);
        expect(viewModel.paymentInfoIsValid()).toBe(false);

        var paymentAmount = viewModel.paymentAmounts().options()[0];
        viewModel.paymentAmounts().selectOption(paymentAmount);
        expect(viewModel.paymentInfoIsValid()).toBe(false);

        viewModel.receiptNumber("123456");
        expect(viewModel.paymentInfoIsValid()).toBe(true);
    });

    it("should submit the data", function() {
        viewModel.patient = {patientId:12};
        var location = viewModel.locations().options()[0];
        viewModel.locations().selectOption(location);
        viewModel.checkinDay("23");
        viewModel.checkinMonth("10");
        viewModel.checkinYear("2012");
        viewModel.checkinHour("13");
        viewModel.checkinMinutes("04");

        var paymentReason = viewModel.paymentReasons().options()[0];
        viewModel.paymentReasons().selectOption(paymentReason);
        var paymentAmount = viewModel.paymentAmounts().options()[0];
        viewModel.paymentAmounts().selectOption(paymentAmount);
        viewModel.receiptNumber("123456");

        var ajaxParams;
        spyOn($, "ajax").andCallFake(function(params) {
            ajaxParams = params;
        });

        expect(viewModel.paymentInfoIsValid()).toBe(true);
        expect(viewModel.checkinInfoIsValid()).toBe(true)

        viewModel.registerCheckin();

        expect($.ajax).toHaveBeenCalled();
        expect(ajaxParams.data).toEqual({
            patientId:12,
            locationId: 1,
            checkinDate: "2012-10-23 13:04:00",
            paymentReasonId: 1,
            paidAmount: 10,
            paymentReceipt: "123456"});
    })

    it("SelectableOptions should have at most 1 selected option", function() {
        expect(viewModel.locations().selectedOption()).toBe(undefined);

        var options = viewModel.locations().options();
        viewModel.locations().selectOption(options[0]);
        expect(viewModel.locations().selectedOption().value).toBe(1);

        viewModel.locations().selectOption(options[1]);
        expect(viewModel.locations().selectedOption().value).toBe(2);
    })
});