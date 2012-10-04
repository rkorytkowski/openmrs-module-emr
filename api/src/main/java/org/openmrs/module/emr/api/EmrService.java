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
package org.openmrs.module.emr.api;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.emr.domain.RadiologyRequisition;

import java.util.List;

/**
 * Public API for EMR-related functionality
 */
public interface EmrService {

    List<Patient> findPatients(String query, Location checkedInAt, Integer start, Integer length);

    Encounter placeRadiologyRequisition(RadiologyRequisition requisition);

}