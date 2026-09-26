INSERT INTO accounts (customer_name, cpf, bank_code, bank_name, branch, account_number) VALUES
    ('Maria Souza',  '43488428095', 1,   'Banco do Brasil',    '0001', '12345-6'),
    ('Joao Pereira', '52998224725', 341, 'Itau Unibanco S.A.', '4521', '98765-4');

INSERT INTO transfers (customer_cpf, source_account, destination_bank_code, destination_bank_name,
                        destination_branch, destination_account, amount, type, fee_applied,
                        total_debited, scheduled_date, requested_at) VALUES
    ('43488428095', '0001-12345-6', 341, 'Itau Unibanco S.A.', '4521', '98765-4',
     1000.00, 'TED', 9.98, 1009.98, DATE '2026-09-25', CURRENT_TIMESTAMP);
