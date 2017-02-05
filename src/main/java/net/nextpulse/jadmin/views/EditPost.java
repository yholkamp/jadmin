package net.nextpulse.jadmin.views;

/**
 * @author yholkamp
 */
public class EditPost {

  private boolean success;
  private String errorMessage;

  public EditPost(boolean success, String errorMessage) {
    this.success = success;
    this.errorMessage = errorMessage;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
