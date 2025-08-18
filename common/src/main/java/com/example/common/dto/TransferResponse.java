package com.example.common.dto;

public record TransferResponse(String transferId, boolean status, String error) {}