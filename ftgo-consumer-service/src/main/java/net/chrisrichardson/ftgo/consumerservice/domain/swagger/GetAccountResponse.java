package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class GetAccountResponse {
  private String accountId;

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public GetAccountResponse() {

  }

  public GetAccountResponse(String accountId) {
    this.accountId = accountId;
  }
}
