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
package org.openmrs.module.emr;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emr.test.builder.ConceptBuilder;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.diagnosis.DiagnosisMetadata;
import org.openmrs.util.OpenmrsUtil;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Various utils to help with testing
 * This class has been copied to the emrapi module except for the setupDiagnosisMetadata method
 */
public class TestUtils {

    /**
     * To test things like: assertContainsElementWithProperty(listOfPatients, "patientId", 2)
     *
     * @param collection
     * @param property
     * @param value
     */
    public static void assertContainsElementWithProperty(Collection<?> collection, String property, Object value) {
        for (Object o : collection) {
            try {
                if (OpenmrsUtil.nullSafeEquals(value, PropertyUtils.getProperty(o, property))) {
                    return;
                }
            } catch (Exception ex) {
                // pass
            }
        }
        Assert.fail("Collection does not contain an element with " + property + " = " + value + ". Collection: "
                + collection);
    }

    public static <T> ArgumentMatcher<T> containsElementsWithProperties(final String property, final T... expectedPropertyValues) {
        return new ArgumentMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                assertTrue(o instanceof Collection);
                Collection actual = (Collection) o;
                for (T expectedPropertyValue : expectedPropertyValues) {
                    assertContainsElementWithProperty(actual, property, expectedPropertyValue);
                }
                return true;
            }
        };
    }

    public static <T> ArgumentMatcher<T> isCollectionOfExactlyElementsWithProperties(final String property, final Object... expectedPropertyValues) {
        return new ArgumentMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                assertTrue(o instanceof Collection);
                Collection actual = (Collection) o;
                assertThat(actual.size(), is(expectedPropertyValues.length));
                for (Object expectedPropertyValue : expectedPropertyValues) {
                    assertContainsElementWithProperty(actual, property, expectedPropertyValue);
                }
                return true;
            }
        };
    }

    /**
     * Tests whether the substring is contained in the actual string.
     */
    public static void assertContains(String substring, String actual) {
        if (substring == null) {
            return;
        }
        if (actual == null) {
            Assert.fail(substring + " is not contained in " + actual);
        }

        if (!actual.contains(substring)) {
            Assert.fail(substring + " is not contained in " + actual);
        }
    }

    /**
     * Tests whether the two strings are equal, ignoring white space and capitalization.
     */
    public static void assertFuzzyEquals(String expected, String actual) {
        if (expected == null && actual == null)
            return;
        if (expected == null || actual == null)
            Assert.fail(expected + " does not match " + actual);
        String test1 = stripWhitespaceAndConvertToLowerCase(expected);
        String test2 = stripWhitespaceAndConvertToLowerCase(actual);
        if (!test1.equals(test2)) {
            Assert.fail(expected + " does not match " + actual);
        }
    }

    /**
     * Tests whether the substring is contained in the actual string. Allows for inclusion of
     * regular expressions in the substring. Ignores white space. Ignores capitalization.
     */
    public static void assertFuzzyContains(String substring, String actual) {
        if (substring == null) {
            return;
        }
        if (actual == null) {
            Assert.fail(substring + " is not contained in " + actual);
        }

        if (!Pattern.compile(stripWhitespaceAndConvertToLowerCase(substring), Pattern.DOTALL).matcher(stripWhitespaceAndConvertToLowerCase(actual)).find()) {
            Assert.fail(substring + " is not contained in " + actual);
        }
    }

    /**
     * Tests whether the substring is NOT contained in the actual string. Allows for inclusion of
     * regular expressions in the substring. Ignores white space. Ignores capitalization.
     */
    public static void assertFuzzyDoesNotContain(String substring, String actual) {
        if (substring == null) {
            return;
        }
        if (actual == null) {
            return;
        }

        if (Pattern.compile(stripWhitespaceAndConvertToLowerCase(substring), Pattern.DOTALL).matcher(stripWhitespaceAndConvertToLowerCase(actual)).find()) {
            Assert.fail(substring + " found in  " + actual);
        }
    }


    private static String stripWhitespaceAndConvertToLowerCase(String string) {
        string = string.toLowerCase();
        string = string.replaceAll("\\s", "");
        return string;
    }

    public static Matcher<Date> isJustNow() {
        return new ArgumentMatcher<Date>() {
            @Override
            public boolean matches(Object o) {
                // within the last second should be safe enough...
                return System.currentTimeMillis() - ((Date) o).getTime() < 1000;
            }
        };
    }

    /**
     * Creates an argument matcher that tests equality based on the equals method, the developer
     * doesn't have to type cast the returned argument when pass it to
     * {@link org.mockito.Mockito#argThat(Matcher)} as it would be the case if we used {@link org.mockito.internal.matchers.Equals} matcher
     *
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> equalsMatcher(final T object) {
        return new ArgumentMatcher<T>() {

            /**
             * @see org.mockito.ArgumentMatcher#matches(java.lang.Object)
             */
            @Override
            public boolean matches(Object arg) {
                return OpenmrsUtil.nullSafeEquals(object, (T) arg);
            }
        };
    }

    public static String join(Iterable<?> iter, String separator) {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (Object o : iter) {
            if (!first) {
                ret.append(separator);
            } else {
                first = false;
            }
            ret.append(o);
        }
        return ret.toString();
    }

    public static String join(Object[] array, String separator) {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (Object o : array) {
            if (!first) {
                ret.append(separator);
            } else {
                first = false;
            }
            ret.append(o);
        }
        return ret.toString();
    }

    /**
     * This is written so it can be moved to a shared class and reused across multiple tests
     */
    public static DiagnosisMetadata setupDiagnosisMetadata(ConceptService conceptService, EmrApiProperties emrApiProperties) {
        ConceptSource emrSource = new EmrActivator().createConceptSources(conceptService);
        ConceptMapType sameAs = conceptService.getConceptMapTypeByName("same-as");

        ConceptDatatype naDatatype = conceptService.getConceptDatatypeByName("N/A");
        ConceptDatatype codedDatatype = conceptService.getConceptDatatypeByName("Coded");
        ConceptDatatype textDatatype = conceptService.getConceptDatatypeByName("Text");

        ConceptClass convSet = conceptService.getConceptClassByName("ConvSet");
        ConceptClass misc = conceptService.getConceptClassByName("Misc");

        Concept primary = new ConceptBuilder(conceptService, naDatatype, misc)
                .addName("Primary")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_PRIMARY).saveAndGet();

        Concept secondary = new ConceptBuilder(conceptService, naDatatype, misc)
                .addName("Secondary")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_SECONDARY).saveAndGet();

        Concept order = new ConceptBuilder(conceptService, codedDatatype, misc)
                .addName("Diagnosis order")
                .addAnswers(primary, secondary)
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER).saveAndGet();

        Concept confirmed = new ConceptBuilder(conceptService, naDatatype, misc)
                .addName("Confirmed")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CERTAINTY_CONFIRMED).saveAndGet();

        Concept presumed = new ConceptBuilder(conceptService, naDatatype, misc)
                .addName("Presumed")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CERTAINTY_PRESUMED).saveAndGet();

        Concept certainty = new ConceptBuilder(conceptService, codedDatatype, misc)
                .addName("Diagnosis certainty")
                .addAnswers(confirmed, presumed)
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CERTAINTY).saveAndGet();

        Concept codedDiagnosis = new ConceptBuilder(conceptService, codedDatatype, misc)
                .addName("Coded diagnosis")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_CODED_DIAGNOSIS).saveAndGet();

        Concept nonCodedDiagnosis = new ConceptBuilder(conceptService, textDatatype, misc)
                .addName("Non-coded diagnosis")
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_NON_CODED_DIAGNOSIS).saveAndGet();

        new ConceptBuilder(conceptService, naDatatype, convSet)
                .addName("Visit diagnoses")
                .addSetMembers(order, certainty, codedDiagnosis, nonCodedDiagnosis)
                .addMapping(sameAs, emrSource, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CONCEPT_SET).saveAndGet();

        return emrApiProperties.getDiagnosisMetadata();
    }
}
