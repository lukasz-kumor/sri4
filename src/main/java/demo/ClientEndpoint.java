package demo;

import io.spring.guides.gs_producing_web_service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class ClientEndpoint {
	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

	private ClientRepository clientRepository;

	@Autowired
	public ClientEndpoint(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getClientRequest")
	@ResponsePayload
	public GetClientResponse getClient(@RequestPayload GetClientRequest request) {
        GetClientResponse response = new GetClientResponse();
		response.setClient(clientRepository.findClient(request.getPesel()));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createClientRequest")
	@ResponsePayload
	public MessageResponse createClient(@RequestPayload CreateClientRequest request) {
		MessageResponse response = new MessageResponse();
		response.setMessage("Wrong parameters");
		if(request.getName() != null || request.getName().length() != 15  || request.getPesel() != null || request.getPesel().length() != 11){
			if(clientRepository.findClient(request.getPesel()) == null){
				Client client = new Client();
				client.setPesel(request.getPesel());
				client.setName(request.getName());
				clientRepository.addClient(client);
				response.setMessage("Client with pesel: " + request.getPesel() + " and name: " + request.getName() + " has been added.");
			}else{
				response.setMessage("Client exists in db.");
			}
		}

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "createLoanRequest")
	@ResponsePayload
	public MessageResponse createLoan(@RequestPayload CreateLoanRequest request) {
		MessageResponse response = new MessageResponse();
		response.setMessage("Wrong parameters");
		if(request.getPesel() != null || request.getAmount() > 100 || request.getDueDays() > 5) {
			response.setMessage(clientRepository.createLoan(request.getPesel(), request.getAmount(),request.getDueDays()));

		}
		return response;
	}
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "payLoanRequest")
	@ResponsePayload
	public MessageResponse payLoan(@RequestPayload PayLoanRequest request) {
		MessageResponse response = new MessageResponse();
		response.setMessage("Wrong parameters");
		if(request.getAmount() > 0){
		response.setMessage(clientRepository.payLoan(request.getPesel(), request.getAmount(), request.getLoanId()));
		}
		return response;
	}
}
