# Getting started

- Spring Batch - Avvio tramite annotazioni
- Spring Batch - Avvio Test `BatchChunkConfigTest.java`
- File - Avvio Test "ReadFromFileSystemConfigTest.java"
- File - Avvio "UploadCsVtoH2Application.java"

## Avvio tramite annotazioni

Il processo che porta all'esecuzione del job e degli step annessi viene innescato dalle seguenti due annotazioni`@Configuration`
e `@EnableBatchProcessing` poste all'interno del file `BatchChunkConfig.java`. La prima ha il compito di dichiarare a Spring che all'interno della classe annotata si trovano
più bean. La seconda genera la configurazione per far partire il processo Batch. 

## Avvio Test `BatchChunkConfigTest.java`

Tramite questo test è possibile lanciare l'applicazione senza che venga inserita anche la parte riguardante la semplice 
lettura del file.

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {BatchChunkConfig.class, UploadCsVtoH2Application.class})
    public class BatchChunkConfigTest {
    
        @Autowired
        private JobLauncherTestUtils jobLauncherTestUtils;
    
        @Test
        public void jobLauncherTestUtils() throws Exception {
            JobExecution jobExecution = jobLauncherTestUtils.launchJob();
            assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        }
    }

## File - Avvio "UploadCsVtoH2Application.java"

All'interno della classe di start-up di spring vi sono il metodo statico `main()` il quale innesca il processo di creazione 
dei vari bean contenuti all'interno dei differenti packages (controller, entity, repository e service).<br> 
Poi vi è variabile `simpleRead` annotata con `@Autowired` che si "lega" all'istanza creata tramite l'annotazione `@Bean` all'interno 
della classe `SimpleRead.java`; infine il secondo metodo `run()` (la cui classe CommandLineRunner fa si che
il bean dovrebbe essere lanciato dentro un'applicazione Spring) chiama il metodo `readFile()` passandogli `evidenceRepository`.
Ciò permette di sfruttare i metodi di JpaRepository per salvare i record letti. <br>
In altre parole all'interno di `UploadCsVtoH2Application.java` viene agganciata a `simpleRead` l'istanza della classe `SimpleRead.java` 
e successivamente lanciato il metodo di lettura all'interno di `run()`. <br> Quando si effettua il lancio dal main è necessario commentare le 
righe `@Configuration` ed `@EnableBatchProcessing` per evitare che vi siano problemi con il processo Batch che, partendo in 
automatico va a scrivere sul DB. Allo stesso modo quando è in funzione il processo Batch commentare il metodo `run()` 
all'interno di `UploadCsVtoH2Application.java`.

    @SpringBootApplication
    public class UploadCsVtoH2Application {

        @Autowired
        SimpleRead simpleRead;
    
        public static void main(String[] args) {
            SpringApplication.run(UploadCsVtoH2Application.class, args);
        }
    
        @Bean
        public CommandLineRunner run(EvidenceRepository evidenceRepository) throws Exception {
            return (String[] args) -> {
                simpleRead.readFile(evidenceRepository);
            };
        }
    }


## File - Avvio Test "ReadFromFileSystemConfigTest.java"

Per testare l'efficacia e poter lanciare uno o l'altro test è stata creata la classe di test `SimpleReadConfigTest.java`
che contiene due variabili: la prima `simpleRead` con l'annotazione `@Autowired` si va ad agganciare al bean 
generato nella classe `SimpleReadConfig.java`. <br> Anche la seconda variabile `eividenceRepository` si aggancia al bean 
tramite `@Autowired`. <br> In entrambi i casi è tramite l'annotazione `@ContextConfiguration(classes = {SimpleReadConfig.class, UploadCsVtoH2Application.class})` 
che vengono avviate le due istanze che poi si collegheranno alle variabili prima citate. <br> In questo modo si evita di andare 
a istanziare anche le classi relative ai processi Batch.
    
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SimpleReadConfig.class, UploadCsVtoH2Application.class})
    public class SimpleReadConfigTest {
    
        @Autowired
        SimpleRead simpleRead;
    
        @Autowired
        EvidenceRepository evidenceRepository;
    
        @Test
        public void simpleRead() {
            this.simpleRead.readFile(evidenceRepository);
            long evidenceTotal = 3000;
            assertEquals(evidenceRepository.findAll().size(), evidenceTotal);
        }
    
    }


# Caricamento dati con Spring Batch

[Link 🔗](https://docs.spring.io/spring-batch/docs/current/reference/html/)

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

```java
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
```

La funzione `public FlatFileItemReader<Evidence> evidenceItemReader()` è incaricata della parsificazione del file,
quindi della suddivisione in token delle varie stringhe.

```java
@Bean
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
}
```

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

## Web Console

- Pulire H2 database in memory: rimuovere `.h2.server.properties` nella home directory
- [localhost:8081/h2-console](http://localhost:8081/h2-console)

# Caricamento dati con plain text file

Per comparare l'efficacia della libreria Batch utilizzo una semplice lettura da file attraverso la creazione della classe
`SimpleRead.java`. Oltre al costruttore questa classe contiene un solo altro metodo che è `readFile()`.

    public void readFile(EvidenceRepository evidenceRepository) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int counter = 0;
            startTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + startTime + " milliseconds");
            while ((line = br.readLine()) != null) {
                if (counter != 0) {
                    String[] arrayList = line.split(",");
                    Evidence evidence = new Evidence(Integer.parseInt(arrayList[0]), arrayList[1], arrayList[2],
                            arrayList[3], arrayList[4], arrayList[5]);
                    evidenceRepository.save(evidence);
                }
                counter++;
            }
            finishTime = System.currentTimeMillis();
            System.out.println("reading starts at - " + finishTime + " milliseconds");
            elapsedTime = (finishTime - startTime) / 1000;
            System.out.println("read execution lasted - " + elapsedTime + "seconds");
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

Insieme alla già citata classe `SimpleRead.java` è stata creata una seconda classe chiamata `SimpleReadConfig.java`.
Questa classe serve per poter inserire, all'interno del flusso di creazione di spring, i vari oggetti che permettono la lettura
del file e la sua archiviazione all'interno del database H2.

    @Configuration
    public class SimpleReaConfig {
        @Bean
        public DataSource getDataSource() {
            System.out.println("creation of DataSource instance");
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("org.h2.Driver");
            dataSourceBuilder.url("jdbc:h2:mem:batchdb");
            dataSourceBuilder.username("sa");
            dataSourceBuilder.password("");
            return dataSourceBuilder.build();
        }
    
        @Bean
        public SimpleRead simpleRead(){
            System.out.println("creation of SimpleRead instance");
            return new ReadFromFileSystem("src/main/resources/MOCK_DATA2.csv");
        }
    }

L'annotazione `@Configuration` dichiara la presenza di più bean all'interno della classe. Il primo bean è quello che crea
l'istanza del DB, andando a settare le principali caratteristiche (url, driver ecc). Il secondo bean invece va a creare
l'istanza della classe `SimpleRead.java`, tramite il costruttore a cui viene passato il path assoluto.
