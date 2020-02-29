package de.openknowledge.projects.todolist.service.domain;

import de.openknowledge.projects.todolist.service.infrastructure.validation.ValidationErrorPayload;

/**
 * Error payload for the entity {@link Todo}.
 */
public final class TodoValidationErrorCodes {

  public static class TitleIsNull extends ValidationErrorPayload {

    public TitleIsNull() {
      super("TITLE_IS_NULL");
    }
  }

  public static class InvalidTitleSize extends ValidationErrorPayload {

    public InvalidTitleSize() {
      super("TITLE_INVALID_SIZE");
    }
  }

  public static class DescriptionTooLong extends ValidationErrorPayload {

    public DescriptionTooLong() {
      super("DESCRIPTION_TOO_LONG");
    }
  }

  public static class DueDateIsNull extends ValidationErrorPayload {

    public DueDateIsNull() {
      super("DUE_DATE_IS_NULL");
    }
  }

  public static class DueDateInvalidPattern extends ValidationErrorPayload {

    public DueDateInvalidPattern() {
      super("DUE_DATE_INVALID_PATTERN");
    }
  }

  public static class DoneIsNull extends ValidationErrorPayload {

    public DoneIsNull() {
      super("DONE_IS_NULL");
    }
  }
}
