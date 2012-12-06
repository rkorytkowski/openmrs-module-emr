package org.openmrs.module.emr.page.controller;

import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RetrospectiveCheckinPageController {

    public void get(@SpringBean("locationService") LocationService locationService,
                           @SpringBean("conceptService") ConceptService conceptService,
                           UiUtils ui,
                           PageModel model) {

        Concept amountPaidConcept = conceptService.getConceptByUuid("5d1bc5de-6a35-4195-8631-7322941fe528");
        model.addAttribute("locations", ui.toJson(getLocations(locationService)));
        model.addAttribute("paymentReasons", ui.toJson(getPaymentReasons(conceptService)));
        model.addAttribute("paymentAmounts", ui.toJson(getPossiblePaymentAmounts()));
    }

    public String post(UiUtils ui,
                       @SpringBean("adtService") AdtService adtService,
                       @SpringBean("conceptService") ConceptService conceptService,
                       @SpringBean("providerService")ProviderService providerService,
                       @SpringBean("emrProperties")EmrProperties emrProperties,
                       @RequestParam("patientId") Patient patient,
                       @RequestParam("locationId") Location location,
                       @RequestParam("checkinDate") Date date,
                       @RequestParam("paymentReasonId") Integer paymentReasonId,
                       @RequestParam("paidAmount") Double paidAmount,
                       @RequestParam("paymentReceipt") String receiptNumber) {

        Collection<Provider> providers = providerService.getProvidersByPerson(Context.getAuthenticatedUser().getPerson());
        Provider checkInClerk = providers.iterator().next();

        Obs paymentReason = createPaymentReasonObservation(conceptService, emrProperties, paymentReasonId);
        Obs paymentAmount = createPaymentAmountObservation(emrProperties, paidAmount);
        Obs paymentReceipt = createPaymentReceiptObservation(emrProperties, receiptNumber);

        adtService.createCheckinInRetrospective(patient, location, checkInClerk, paymentReason, paymentAmount, paymentReceipt, date);
        return "redirect:" + ui.pageLink("mirebalais", "home");
    }

    private Obs createPaymentReceiptObservation(EmrProperties emrProperties, String receiptNumber) {
        Obs paymentReceipt = new Obs();
        paymentReceipt.setConcept(emrProperties.getPaymentReceiptNumberConcept());
        paymentReceipt.setValueText(receiptNumber);
        return paymentReceipt;
    }

    private Obs createPaymentAmountObservation(EmrProperties emrProperties, Double paidAmount) {
        Obs paymentAmount = new Obs();
        paymentAmount.setConcept(emrProperties.getPaymentAmountConcept());
        paymentAmount.setValueNumeric(paidAmount);
        return paymentAmount;
    }

    private Obs createPaymentReasonObservation(ConceptService conceptService, EmrProperties emrProperties, Integer paymentReasonId) {
        Obs paymentReason = new Obs();
        paymentReason.setConcept(emrProperties.getPaymentReasonsConcept());
        paymentReason.setValueCoded(conceptService.getConcept(paymentReasonId));
        return paymentReason;
    }

    private List<SimpleObject> getPossiblePaymentAmounts() {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        SimpleObject exempt = new SimpleObject();
        exempt.put("value", 0);
        exempt.put("label", "Exempt");

        SimpleObject fiftyGourdes = new SimpleObject();
        fiftyGourdes.put("value", 50);
        fiftyGourdes.put("label", "50 Gourdes");

        SimpleObject hundredGourdes = new SimpleObject();
        hundredGourdes.put("value", 100);
        hundredGourdes.put("label", "100 Gourdes");

        items.add(hundredGourdes); items.add(fiftyGourdes); items.add(exempt);
        return items;
    }

    private List<SimpleObject> getPaymentReasons(ConceptService conceptService) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Concept paymentReason = conceptService.getConceptByUuid("36ba7721-fae0-4da4-aef2-7e476cc04bdf");
        Collection<ConceptAnswer> paymentReasonAnswers = paymentReason.getAnswers();
        for(ConceptAnswer reason : paymentReasonAnswers) {
            Concept answerConcept = reason.getAnswerConcept();
            SimpleObject item = new SimpleObject();
            item.put("value", answerConcept.getConceptId());
            item.put("label", answerConcept.getName().getName());
            items.add(item);
        }
        return items;
    }

    private List<SimpleObject> getLocations(LocationService locationService) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        List<Location> locations = locationService.getAllLocations(false);
        for (Location location: locations) {
            SimpleObject item = new SimpleObject();
            item.put("value", location.getLocationId());
            item.put("label", location.getName());
            items.add(item);
        }
        return items;

    }
}
