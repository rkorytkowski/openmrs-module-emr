/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emr.paperrecord.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.emr.api.db.hibernate.HibernateSingleClassDAO;
import org.openmrs.module.emr.paperrecord.PaperRecordRequest;

import java.util.List;

public class HibernatePaperRecordRequestDAO  extends HibernateSingleClassDAO<PaperRecordRequest> implements PaperRecordRequestDAO {

    public HibernatePaperRecordRequestDAO() {
        super(PaperRecordRequest.class);
    }

    @Override
    public List<PaperRecordRequest> findPaperRecordRequests(List<PaperRecordRequest.Status> statusList, Patient patient, Location recordLocation, String identifier, Boolean hasIdentifier) {

        Criteria criteria = createPaperRecordCriteria();

        if (statusList != null) {
            addStatusDisjunctionRestriction(criteria, statusList);
        }

        if (patient != null) {
            addPatientRestriction(criteria, patient);
        }

        if (recordLocation != null) {
            addRecordLocationRestriction(criteria, recordLocation);
        }

        if (identifier != null) {
            addIdentifierRestriction(criteria, identifier);
        }

        if (hasIdentifier != null) {
            addHasIdentifierRestriction(criteria, hasIdentifier);
        }

        addOrderByDate(criteria);

        return (List<PaperRecordRequest>) criteria.list();
    }

    private Criteria createPaperRecordCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(PaperRecordRequest.class);
    }

    private void addStatusRestriction(Criteria criteria, PaperRecordRequest.Status status) {
        criteria.add(Restrictions.eq("status", status));
    }

    private void addStatusDisjunctionRestriction(Criteria criteria, List<PaperRecordRequest.Status> statusList) {

        if (statusList.size() == 1) {
            criteria.add(Restrictions.eq("status", statusList.get(0)));
        }
        else {
            Disjunction statusDisjunction = Restrictions.disjunction();

            for (PaperRecordRequest.Status status : statusList) {
                statusDisjunction.add(Restrictions.eq("status", status));
            }

            criteria.add(statusDisjunction);
        }
    }

    private void addPatientRestriction(Criteria criteria, Patient patient) {
        criteria.add(Restrictions.eq("patient", patient));
    }

    private void addRecordLocationRestriction(Criteria criteria, Location recordLocation) {
        criteria.add(Restrictions.eq("recordLocation", recordLocation));

    }

    private void addIdentifierRestriction(Criteria criteria, String identifier) {
        criteria.add(Restrictions.eq("identifier", identifier));
    }

    private void addHasIdentifierRestriction(Criteria criteria, boolean hasIdentifier) {
        if (hasIdentifier) {
            criteria.add(Restrictions.isNotNull("identifier"));
        }
        else {
            criteria.add(Restrictions.isNull("identifier"));
        }
    }

    private void addOrderByDate(Criteria criteria) {
        criteria.addOrder(Order.asc("dateCreated"));
    }

}
