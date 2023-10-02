package io.angularpay.paynable.util;

import io.angularpay.paynable.domain.Investor;
import io.angularpay.paynable.domain.PaynableRequest;

public class InvestmentPartyMaturityMessageGenerator {

    public static String formatInvesteeSms(PaynableRequest request) {
        // TODO: placeholder for now, should be better formatted
        return String.format(
                "Your Loan request, %s, of %s %s matures on %s",
                request.getRequestTag(),
                request.getAmount().getCurrency(),
                String.format("%,.2f",Double.parseDouble(request.getAmount().getValue())),
                request.getMaturesOn()
        );
    }

    public static String formatInvesteeEmail(PaynableRequest request) {
        // TODO: placeholder for now, should be better formatted
        return String.format(
                "Your Loan request, %s, of %s %s matures on %s",
                request.getRequestTag(),
                request.getAmount().getCurrency(),
                String.format("%,.2f",Double.parseDouble(request.getAmount().getValue())),
                request.getMaturesOn()
        );
    }

    public static String formatInvestorSms(PaynableRequest request, Investor investor) {
        // TODO: placeholder for now, should be better formatted
        return String.format(
                "Your Loan investment, %s, of %s %s will be paid on %s",
                request.getRequestTag(),
                investor.getAmount().getCurrency(),
                String.format("%,.2f",Double.parseDouble(investor.getAmount().getValue())),
                request.getMaturesOn()
        );
    }

    public static String formatInvestorEmail(PaynableRequest request, Investor investor) {
        // TODO: placeholder for now, should be better formatted
        return String.format(
                "Your Loan investment, %s, of %s %s will be paid on %s",
                request.getRequestTag(),
                investor.getAmount().getCurrency(),
                String.format("%,.2f",Double.parseDouble(investor.getAmount().getValue())),
                request.getMaturesOn()
        );
    }

}
