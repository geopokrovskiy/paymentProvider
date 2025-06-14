This is a fully functional payment provider for simulating real-world payment transactions such as Top-Up (Deposit) and Payout (Withdrawal) using Spring WebFlux and Java 21.

The service includes:
- Secure transaction endpoints (with Basic Auth)
- Real-time webhook notifications for status updates
- Card-based top-up and payout flows
- Daily & range-based transaction history
- Transaction detail endpoints
- Webhooks are triggered on every transaction state change



API Examples
- POST /api/v1/merhants/register/
- GET /api/v1/payments/accounts/get_account_list/
- POST /api/v1/payments/top_up/
- POST /api/v1/payments/pay_out/
