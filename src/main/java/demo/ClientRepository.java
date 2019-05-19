package demo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.spring.guides.gs_producing_web_service.Client;
import io.spring.guides.gs_producing_web_service.Loan;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class ClientRepository {
	private static final List<Client> clientList= new ArrayList<>();

	@PostConstruct
	public void initData() {
	    Loan loan = new Loan();

		Client client = new Client();
        loan.setDueDays(10);
        loan.setLoanAmount(1000);
        loan.setLoanId(1);
        loan.setPaidBack(false);

        client.setName("Andrzej");
        client.setPesel("95083011135");
        client.addLoan(loan);

        clientList.add(client);

	}

	public Client findClient(String pesel) {
		Assert.notNull(pesel, "The client's pesel must not be null");

		for(Client client: clientList){
		    if(client.getPesel().equals(pesel)){
		        return client;
            }
        }
		return null;
	}
	public void addClient(Client client){
		clientList.add(client);
	}
	public Integer createLoanId(){
		Integer loanId = 0;
		for(Client cl : clientList){
			for(Loan lo : cl.getLoan()){
				if(loanId < lo.getLoanId()){
					loanId = lo.getLoanId();
				}
			}
		}
		return loanId+1;
	}

	public Double calculateFee(String pesel){
		Client client = findClient(pesel);
		Integer dueDaysSummary = 0;
		Integer loanSummary = 0;

		for(Loan lo : client.getLoan()){
			dueDaysSummary += lo.getDueDays();
			loanSummary += lo.getLoanAmountToPayback();
		}
		if(dueDaysSummary < 15 && loanSummary < 10000){
			return 0.1;
		}else if(dueDaysSummary < 30 && loanSummary < 25000){
			return 0.2;
		}else if(dueDaysSummary < 50 && loanSummary < 50000){
			return 0.3;
		}else if(dueDaysSummary < 100 && loanSummary < 100000){
			return 0.4;
		}
		return 1.0;
	}
	public String createLoan(String pesel, int amount, int dueDays){

		//get last loanId
		Client client = findClient(pesel);

		if(client == null){
			return "Client does not exist";
		}
		if(dueDays < 5 || dueDays > 100){
			return "Either too long or too short due.";
		}
		Double fee = calculateFee(pesel);
		if(fee == 1) return "NO LOAN FOR YOU, SORRY";

		Loan loan = new Loan();
		loan.setLoanId(createLoanId());
		loan.setLoanAmount(amount);
		loan.setLoanAmountToPayback((int)Math.round(amount + amount * fee));
		loan.setDueDays(dueDays);
		client.addLoan(loan);

		clientList.set(clientList.indexOf(client), client);
		return "Loan added";
	}
	public String payLoan(String pesel, int amount, int loanId){
		Client client = findClient(pesel);
		if(client == null) return "No such client.";
		for(Loan lo : client.getLoan()){
		    System.out.println(lo.getLoanId() + " / "  + loanId + " /size: " + client.getLoan().size());
			if(lo.getLoanId() == loanId){
				if(lo.getLoanAmountToPayback() > 0){
					lo.setLoanAmountToPayback(lo.getLoanAmountToPayback() - amount);
					if(lo.getLoanAmountToPayback() < 0 ){
                        lo.setPaidBack(true);
                        clientList.set(clientList.indexOf(client), client);
                        return "Fine. " + lo.getLoanAmountToPayback() * (-1) + "$ overpaid. Please withdraw.";
                    }
                    clientList.set(clientList.indexOf(client), client);
                    return "Fine. " + lo.getLoanAmountToPayback() + "$ more to pay on that loan.";
				}else{
					return "This loan is already paid.";
				}

			}
		}

		return "No such loan.";
	}

	public void minusDueDays(){
        for(Client cl : clientList){
            for(Loan lo : cl.getLoan()){
                if(lo.getPaidBack() == false){
                    lo.setDueDays(lo.getDueDays()-1);

                }
            }
        }
    }
}
