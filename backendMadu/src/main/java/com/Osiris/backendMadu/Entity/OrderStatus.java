package com.Osiris.backendMadu.Entity;

public enum OrderStatus {
    PENDING,       // enviado a WhatsApp
    CONFIRMED,  // confirmado por chat
    SHIPPED,
    CANCELLED,
    COMPLETED
}
