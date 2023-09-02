package local.loeches.restfull;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import local.loeches.restfull.model.Employee;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend")
public class GreetingController {
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	private static final Logger LOGGER = LogManager.getLogger(GreetingController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ICliente clienteRepo;
    
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		LOGGER.info("Logger desde GreetingController");
		LOGGER.error("error desde GreetingController");
		System.out.println("Salida por consola!!");
		return new Greeting(counter.incrementAndGet(), String.format(template, name));

	}
//	template="Goodbye, %s!";
	@GetMapping("/farewell")
	public Greeting farewell(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format("Goodbye, %s!", name));
	}
	
	@GetMapping("/insertar")
	public int insertar(@RequestParam(value = "nombre") String nombre, @RequestParam(value = "email") String email ) {
		String sql = "INSERT INTO cliente (nombre, email) VALUES ('"
                + nombre +"','" + email +"')";
		System.out.println(sql);
        int rows = jdbcTemplate.update(sql);
//		int rows=1;
        if (rows > 0) {
            System.out.println("A new row has been inserted.");
        }
        return rows;
	}
	
    @GetMapping("/listar")
    public List<CCliente> listAll(Model model) {
    	
        List<CCliente> listClientes = clienteRepo.findAll();
        model.addAttribute("listClientes", listClientes);
           
        return listClientes;
    }
    
    @GetMapping("/listar/{id}")
    public ResponseEntity<CCliente> get(@PathVariable Integer id){
    	try { 
    	CCliente cliente = clienteRepo.findById(id).get();
    	return new ResponseEntity<CCliente>(cliente,HttpStatus.OK);
    	    	}
    	catch (NoSuchElementException e) {
    		return new ResponseEntity<CCliente>(HttpStatus.NOT_FOUND);
    	}
    	
    	
    	
    	
    }

	@GetMapping("/report")
	public ResponseEntity<byte[]> getEmployeeRecordReport() {

		try {
			// create employee data
			Employee emp1 = new Employee(1, "AAA", "BBB", "A city");
			Employee emp2 = new Employee(2, "XXX", "ZZZ", "B city");

			List<Employee> empLst = new ArrayList<Employee>();
			empLst.add(emp1);
			empLst.add(emp2);

			//dynamic parameters required for report
			Map<String, Object> empParams = new HashMap<String, Object>();
			empParams.put("CompanyName", "TechGeekNext");
			empParams.put("employeeData", new JRBeanCollectionDataSource(empLst));

			JasperPrint empReport =
					JasperFillManager.fillReport
							(
									JasperCompileManager.compileReport(
											ResourceUtils.getFile("classpath:employees-details.jrxml")
													.getAbsolutePath()) // path of the jasper report
									, empParams // dynamic parameters
									, new JREmptyDataSource()
							);

			HttpHeaders headers = new HttpHeaders();
			//set the PDF format
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "employees-details.pdf");
			//create the report in PDF format
			return new ResponseEntity<byte[]>
					(JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);

		} catch(Exception e) {
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
