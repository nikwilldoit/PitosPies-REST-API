package com.pitospies.pitospieswebservice.models;

import com.pitospies.pitospieswebservice.models.validators.AtLeastOneItemInOrderConstraint;
import com.pitospies.pitospieswebservice.models.validators.OrderAreaExistsConstraint;
import com.pitospies.pitospieswebservice.models.validators.OrderItemValuesConstraint;
import com.pitospies.pitospieswebservice.models.validators.PieNamesExistsConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private Integer id;
    @NotNull(message="Το Ονοματεπώνυμο δεν πρέπει να είναι null")
    @NotEmpty(message="Το Ονοματεπώνυμο δεν πρέπει να είναι κενό")
    private String fullname;
    @NotNull(message="Η διεύθυνση δεν πρέπει να είναι null")
    @NotEmpty(message="Η διεύθυνση δεν πρέπει να είναι κενή")
    private String address;
    @NotNull(message="Το email πρέπει να είναι συμπληρωμένο")
    @Email
    private String email;
    @Pattern(regexp = "^(2|6)[0-9]{9}$", message = "Μη Έγκυρο Τηλέφωνο")
    private String tel;
    private String comments;
    @NotNull(message="Πρέπει να έχει επιλεχθεί τιμή για την προσφορά")
    private Boolean offer;
    @Pattern(regexp = "^(visa|cash)$", message="O τρόπος πληρωμής πρέπει να είναι visa ή cash ")
    private String payment;
    private LocalDateTime stamp;

    @Transient
    @OrderAreaExistsConstraint
    private Area area;

    @Transient
    @PieNamesExistsConstraint(message="Ζητήσατε πίτα που δεν υπάρχει")
    @AtLeastOneItemInOrderConstraint(message="Πρέπει να παραγγείλετε τουλάχιστον μία πίτα")
    @OrderItemValuesConstraint(message="Το πλήθος μιας συγκεκριμένης πίτας πρέπει να είναι 1-100")
    private List<OrderItem> orderItems;
}