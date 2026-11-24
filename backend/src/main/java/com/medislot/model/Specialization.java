package com.medislot.model;

public enum Specialization {
    CARDIOLOGY("Cardiology", "Heart & Cardiovascular Care"),
    DERMATOLOGY("Dermatology", "Skin, Hair & Nail Disorders"),
    DENTISTRY("Dentistry", "Dental, Oral Surgery & Hygiene"),
    NEUROLOGY("Neurology", "Brain & Nervous System"),
    ORTHOPEDICS("Orthopedics", "Bones, Joints & Musculoskeletal"),
    PEDIATRICS("Pediatrics", "Infant, Child & Adolescent Care"),
    GYNECOLOGY("Gynecology", "Women's Health & Obstetrics"),
    ENT("ENT", "Ear, Nose, Throat & Head Care"),
    OPHTHALMOLOGY("Ophthalmology", "Eye & Vision Medical Care"),
    PSYCHIATRY("Psychiatry", "Mental Health & Behavioral Therapy"),
    GENERAL_MEDICINE("General Medicine", "Primary Care & Preventative Health"),
    UROLOGY("Urology", "Urinary Tract & Male Reproductive"),
    GASTROENTEROLOGY("Gastroenterology", "Digestive & Gastrointestinal Health"),
    PULMONOLOGY("Pulmonology", "Lungs & Respiratory System"),
    ENDOCRINOLOGY("Endocrinology", "Hormones, Diabetes & Metabolism"),
    ONCOLOGY("Oncology", "Cancer Care & Targeted Therapy"),
    RADIOLOGY("Radiology", "Diagnostic Imaging & Interventions"),
    NEPHROLOGY("Nephrology", "Kidney Care & Renal Disorders");

    private final String displayName;
    private final String description;

    Specialization(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
