/**
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
package org.openmrs.module.emr.api.impl;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.emr.EmrProperties;
import org.openmrs.module.emr.adt.AdtService;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.api.db.EmrDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class EmrServiceImpl extends BaseOpenmrsService implements EmrService {

    private EmrDAO dao;

    @Autowired
    @Qualifier("emrProperties")
    private EmrProperties emrProperties;

    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Autowired
    @Qualifier("adtService")
    private AdtService adtService;

    public void setDao(EmrDAO dao) {
        this.dao = dao;
    }

    public void setEmrProperties(EmrProperties emrProperties) {
        this.emrProperties = emrProperties;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setAdtService(AdtService adtService) {
        this.adtService = adtService;
    }

    @Override
    public List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length) {
        return dao.findPatients(query, checkedInAt, start, length);
    }

}
