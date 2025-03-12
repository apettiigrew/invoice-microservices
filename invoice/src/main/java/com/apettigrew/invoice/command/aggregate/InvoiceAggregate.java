package com.apettigrew.invoice.command.aggregate;

import com.apettigrew.invoice.command.CreateInvoiceCommand;
import com.apettigrew.invoice.command.DeleteInvoiceCommand;
import com.apettigrew.invoice.command.UpdateInvoiceCommand;
import com.apettigrew.invoice.command.event.InvoiceCreatedEvent;
import com.apettigrew.invoice.command.event.InvoiceDeletedEvent;
import com.apettigrew.invoice.command.event.InvoiceUpdatedEvent;
import com.apettigrew.invoice.dtos.AddressDto;
import com.apettigrew.invoice.respositories.InvoiceRepository;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Aggregate
@NoArgsConstructor
public class InvoiceAggregate {

    @AggregateIdentifier
    private String id;
    private LocalDate paymentDue;
    private String description;
    private Integer paymentTerms;
    private String clientName;
    private String clientEmail;
    private AddressDto senderAddress;
    private AddressDto clientAddress;
    private String status;
    private BigDecimal total;
    private boolean activeSw;

    @CommandHandler
    public InvoiceAggregate(CreateInvoiceCommand createInvoiceCommand, InvoiceRepository invoiceRepository) {
        /*Optional<Customer> optionalCustomer = customerRepository.
                findByMobileNumberAndActiveSw(createCustomerCommand.getMobileNumber(), true);
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    + createCustomerCommand.getMobileNumber());
        }*/
        InvoiceCreatedEvent invoiceCreatedEvent = new InvoiceCreatedEvent();
        BeanUtils.copyProperties(createInvoiceCommand, invoiceCreatedEvent);
        AggregateLifecycle.apply(invoiceCreatedEvent);
    }

    @EventSourcingHandler
    public void on(InvoiceCreatedEvent invoiceCreatedEvent) {
        this.id = invoiceCreatedEvent.getId();
        this.paymentDue = invoiceCreatedEvent.getPaymentDue();
        this.description = invoiceCreatedEvent.getDescription();
        this.paymentTerms = invoiceCreatedEvent.getPaymentTerms();
        this.clientName = invoiceCreatedEvent.getClientName();
        this.clientEmail = invoiceCreatedEvent.getClientEmail();
        this.senderAddress = invoiceCreatedEvent.getSenderAddress();
        this.clientAddress = invoiceCreatedEvent.getClientAddress();
        this.status = invoiceCreatedEvent.getStatus();
        this.total = invoiceCreatedEvent.getTotal();
        this.activeSw = invoiceCreatedEvent.isActiveSw();
    }

    @CommandHandler
    public void handle(UpdateInvoiceCommand updateInvoiceCommand, EventStore eventStore) {
        /*List<?>   commands = eventStore.readEvents(updateCustomerCommand.getCustomerId()).asStream().toList();
        if(commands.isEmpty()) {
            throw new ResourceNotFoundException("Customer", "customerId", updateCustomerCommand.getCustomerId());
        }*/
        InvoiceUpdatedEvent invoiceUpdatedEvent = new InvoiceUpdatedEvent();
        BeanUtils.copyProperties(updateInvoiceCommand, invoiceUpdatedEvent);
        AggregateLifecycle.apply(invoiceUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(InvoiceUpdatedEvent paymentDue) {
        this.paymentDue = paymentDue.getPaymentDue();
        this.description = paymentDue.getDescription();
        this.paymentTerms = paymentDue.getPaymentTerms();
        this.clientName = paymentDue.getClientName();
        this.clientEmail = paymentDue.getClientEmail();
        this.senderAddress = paymentDue.getSenderAddress();
        this.clientAddress = paymentDue.getClientAddress();
        this.status = paymentDue.getStatus();
        this.total = paymentDue.getTotal();
        this.activeSw = paymentDue.isActiveSw();
    }

    @CommandHandler
    public void handle(DeleteInvoiceCommand deleteInvoiceCommand) {
        InvoiceDeletedEvent invoiceDeletedEvent = new InvoiceDeletedEvent();
        BeanUtils.copyProperties(deleteInvoiceCommand, invoiceDeletedEvent);
        AggregateLifecycle.apply(invoiceDeletedEvent);
    }

    @EventSourcingHandler
    public void on(InvoiceDeletedEvent invoiceDeletedEvent) {
        this.activeSw = invoiceDeletedEvent.isActiveSw();
    }
}
