
<h1>Spring Batch</h1> 
<a href="https://docs.spring.io/spring-batch/docs/current/reference/html/">Spring Batch Documentation</a>

E’ una libreria utilizzata per il processamento di grandi quantità di dati in maniera automatica.
Un programma batch generalmente può fare le seguenti operazioni <br><br>
● Legge un numero (elevato) di record da un database, un file o una coda. <br>
● Può elaborarli in qualche modo. <br>
● Scrive i dati in un database, file o coda. <br><br>
Nel nostro caso servirebbe a caricare i dati del csv all’interno del database in memory
all’avvio dell’applicazione, in modo più rapido che con una semplice lettura da file.
La libreria si basa principalmente su due entità che sono il Job (un processo batch) che fa
da contenitore per la seconda entità che è lo Step. Quest’ultimo è colui che effettivamente
andrà a svolgere il lavoro dei lettura. Quindi il Job lancia lo Step il quale a sua volta chiama
le funzioni `evidenceItemReader()`, `evidenceItemProcessor()` e `evidenceItemWriter()`. <br><br>
    
    @Bean
    public Job loadEvidenceJob(Step step1) throws Exception {
        return jobBuilderFactory.get("loadEvidenceJob")
            .incrementer(new RunIdIncrementer())
            .listener(new JobResultListener())
            .flow(step1)
            .end()
            .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Evidence> evidenceItemWriter) {
        return stepBuilderFactory.get("step1")
                .<Evidence, Evidence>chunk(5)
                .reader(evidenceItemReader())
                .processor(evidenceItemProcessor())
                .writer(evidenceItemWriter)
                .build();
    }

La funzione `public FlatFileItemReader<Evidence> evidenceItemReader()` è incaricata della parsificazione del file, quindi della suddivisione
in token delle varie stringhe.

    `@Bean
    public FlatFileItemReader<Evidence> evidenceItemReader() {
        FlatFileItemReader<Evidence> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("MOCK_DATA2.csv"));
        reader.setLinesToSkip(1);
        
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("id", "first_name", "last_name", "email", "gender", "ip_address");
        delimitedLineTokenizer.setDelimiter(",");
        
        EvidenceFieldSetMapper fieldSetMapper = new EvidenceFieldSetMapper();
        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        
        reader.setLineMapper(defaultLineMapper);
        return reader;
    }`
Il valore di ritorno è un oggetto di tipo FlatFileItemReader, questa classe permette di settare
il nome della risorsa da recuperare tramite il metodo `setResources()`. Vi è inoltre la possibilità
di saltare la riga di header (del file da parsificare) tramite `setLinesToSkip()`. <br>
L’impostazione per la divisione in token viene fatta tramite l’oggetto di classe
DelimitedLineTokenizer, il quale permette di decidere quale delimitatore utilizzare; inoltre
vengono settati i nomi dei campi a cui apparterranno i vari token tramite il metodo `setNames()`.
Infine l’oggetto di classe DefaultLineMapper, tramite il metodo `setLineTokenizer()` va a
“tokenizare” la stringa nei vari FieldSet (impostati al passo precedente) e successivamente li
mappa sull’oggettto da noi scelto tramite il metodo `setFieldSetMapper()`.
All’interno della classe EvidenceFieldSetMapper vi è proprio la mappatura dei campi sugli
attributi dell’oggetto scelto

    @Override
    public Evidence mapFieldSet(FieldSet fieldSet) {
        final Evidence evidence = new Evidence();
        evidence.setId(Integer.parseInt(fieldSet.readString("id")));
        evidence.setFirst_name(fieldSet.readString("first_name"));
        ...
    }
Il passo successivo alla lettura è quello della manipolazione dei dati appena letti, tramite la
funzione evidenceItemProcessor(). Nel nostro caso non è stata necessaria nessuna modifica dei dati.

    @Bean
    public ItemProcessor<Evidence, Evidence> evidenceItemProcessor() {
        return new Processor();
    }
Infine si termina con la funzione evidenceItemWriter() che ha il compito di generare un output su di un mezzo persistente. 
Il caso nostro è quello di un database embedded, in particolare H2. 
Per il controllo dell’effettivo inserimento dei dati si può accedere alla console di H2 tramite “localhost:8081/h2-console”.
    
    @Bean
    public JdbcBatchItemWriter<Evidence> evidenceItemWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Evidence>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO evidence (id, first_name, last_name, email, gender, ip_address) VALUES (:id, :first_name, :last_name, :email, :gender, :ip_address)")
            .dataSource(dataSource)
            .build();
    }`
La decisione di utilizzare questa libreria piuttosto che una semplice lettura da file è stata determinata dopo il testing 
fatto su un medesimo file di 3000 righe “MOCK_DATA2.csv”.
La lettura dal file-system è stata implementata tramite una API “localhost:8081/load-from-fileSystem” che fa partire la 
procedura di lettura del file. Il tempo impiegato dalla lettura dal file-system è di circa 8 sec, mentre la lettura tramite la
libreria Batch è circa meno di 1 sec. <br>
E’ presumibile che con l‘aumentare dei dati il divario si intensifichi, visto che l’obbiettivo della suddetta sono grandi 
moli di dati.
Se si vuole testare la lettura da file-system è necessario commentare le righe
`@Configuration` e `@EnableBatchProcessing` e dopo aver fatto ripartire il progetto andare
all’API descritta precedentemente.
Di base quando il progetto viene lanciato carica il Db tramite la libreria Batch.