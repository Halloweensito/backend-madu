package com.Osiris.backendMadu.Entity;

public enum PaymentMethod {
    CASH,             // Efectivo (en local)
    BANK_TRANSFER,    // Transferencia bancaria
    CREDIT_CARD,      // Tarjeta de Crédito
    DEBIT_CARD,       // Tarjeta de Débito (¡Faltaba esta!)
    MERCADOPAGO,      // Dinero en cuenta de MercadoPago / QR
}
