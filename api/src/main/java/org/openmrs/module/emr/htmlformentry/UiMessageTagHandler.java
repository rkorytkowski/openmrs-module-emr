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

package org.openmrs.module.emr.htmlformentry;

import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.htmlformentry.BadFormDesignException;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionController;
import org.openmrs.module.htmlformentry.handler.SubstitutionTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class UiMessageTagHandler extends SubstitutionTagHandler {

    MessageSourceService messageSourceService;
    Locale locale;

    public UiMessageTagHandler() {
        messageSourceService = Context.getMessageSourceService();
        locale = Context.getLocale();
    }

    public UiMessageTagHandler(MessageSourceService messageSourceService, Locale locale) {
        this.messageSourceService = messageSourceService;
        this.locale = locale;
    }

    @Override
    protected String getSubstitution(FormEntrySession session, FormSubmissionController controllerActions, Map<String, String> parameters) throws BadFormDesignException {
        String codeParam = parameters.get("code");
        if (codeParam == null) {
            return "Missing \"code\" attribute";
        }

        List<String> argList = new ArrayList<String>();
        int index = 0;
        while (true) {
            String argValue = parameters.get("arg" + index);
            if (argValue == null) {
                break;
            }
            argList.add(argValue);
            ++index;
        }
        Object[] args = argList.isEmpty() ? null : argList.toArray();
        return messageSourceService.getMessage(codeParam, args, locale);
    }
}