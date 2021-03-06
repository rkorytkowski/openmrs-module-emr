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

package org.openmrs.module.emr.page.controller.radiology;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.api.EmrService;
import org.openmrs.module.emr.radiology.RadiologyProperties;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OrderXrayPageController {

    public void controller(@RequestParam("patientId") Patient patient,
                           @SpringBean("radiologyProperties") RadiologyProperties radiologyProperties,
                           @SpringBean("emrService") EmrService emrService,
                           UiUtils ui,
                           PageModel model) {

        Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(Context.getAuthenticatedUser().getPerson());
        model.addAttribute("currentProvider", providers.iterator().next());

        model.addAttribute("xrayOrderables", ui.toJson(getXrayOrderables(radiologyProperties, Context.getLocale())));
        model.addAttribute("portableLocations", ui.toJson(getPortableLocations(emrService, ui)));
        model.addAttribute("patient", patient);
    }

    private List<SimpleObject> getPortableLocations(EmrService emrService, final UiUtils ui) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        List<Location> locations = emrService.getLoginLocations(); // TODO: is login locations really the right thing here?

        // sort objects by localized name
        Collections.sort(locations, new ByFormattedObjectComparator(ui));

        for (Location location: locations) {
            SimpleObject item = new SimpleObject();
            item.put("value", location.getLocationId());
            item.put("label", ui.format(location));
            items.add(item);
        }
        return items;

    }

    private List<SimpleObject> getXrayOrderables(RadiologyProperties radiologyProperties, Locale locale) {
        List<SimpleObject> items = new ArrayList<SimpleObject>();
        Concept xrayOrderable = radiologyProperties.getXrayOrderablesConcept();
        for (Concept concept : xrayOrderable.getSetMembers()) {
            SimpleObject item = new SimpleObject();
            item.put("value", concept.getId());

            // TODO: this should really be fully specified name based on local
            item.put("label", concept.getName().getName());
            items.add(item);
        }
        return items;
    }

}
