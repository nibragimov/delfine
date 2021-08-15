1. use file "v213532_rawdata_10m_2018 small.csv" and store it in neo4j import folder.
2. run the content of file "Neo4j DDL.txt" in neo4j to create local ontology schema.
3. the database schema looks like file "latest_neoj_graph".
4. run the content of file "Neo4j Extraction.txt" in neo4j to cypher out data part to map with public ontologies like OntoWind.
5. running above query we get output file "export_for_refine", which will be uploaded to OpenRefine.
6. after mapping classes in open refine the file "export_for_refine.ttl" can be extracted from OpenRefine as final wind data in the format of OntoWind.