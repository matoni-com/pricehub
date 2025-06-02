CREATE TABLE processed_files (
  retail_chain_id BIGINT NOT NULL,
  file_name VARCHAR NOT NULL,
  processed_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (retail_chain_id, file_name),
  FOREIGN KEY (retail_chain_id) REFERENCES retail_chains(id)
);
