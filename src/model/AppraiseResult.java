package model;

/**
 * Appraisal Result
 * Represents the possible outcomes of an artifact appraisal operation.
 */
public enum AppraiseResult
{
    SUCCESS,        // Appraisal completed successfully
    NO_ENERGY,      // Insufficient energy to perform appraisal
    ALREADY_DONE    // Artifact has already been appraised
}