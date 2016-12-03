package pt.ulusofona.es.num_aluno.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;
import pt.ulusofona.es.num_aluno.data.Despesa;
import pt.ulusofona.es.num_aluno.form.DespesaForm;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@Transactional
public class FormController {

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHome(ModelMap model) {
        return "home";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList(ModelMap model) {

        List<Despesa> despesas = em.createQuery("select d from Despesa d order by d.data DESC", Despesa.class).getResultList();
        model.put("despesas", despesas);
        return "list";
    }


    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String getForm(ModelMap model) {
        model.put("despesaForm", new DespesaForm());
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@Valid @ModelAttribute("despesaForm") DespesaForm despesaForm,
                             BindingResult bindingResult,
                             ModelMap model) {

        if (bindingResult.hasErrors()) {
            return "form";
        }

        Despesa despesa;
        if(despesaForm.getId() != null) {
            despesa = em.find(Despesa.class, despesaForm.getId());
        } else {
            despesa = new Despesa();
        }

        String despesaCategoria = StringUtils.strip(despesaForm.getCategoria(), ",");
        despesa.setCategoria(despesaCategoria);
        despesa.setData(despesaForm.getData());
        despesa.setDescricao(despesaForm.getDescricao());
        despesa.setValor(despesaForm.getValor());
        despesa.setLocalizacao(despesaForm.getLocalizacao());
        em.persist(despesa);

        model.addAttribute("message", "Sucesso! A despesa de " + despesa.getCategoria() + " no dia " + despesa.getData() +
                " foi gravada na BD e foi-lhe atribuído o ID " + despesa.getId());
        return "result";
    }

    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public String getInfo(ModelMap model, @PathVariable("id") Long id) {

        Despesa despesa = em.find(Despesa.class, id);

        model.put("despesa", despesa);

        return "info";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable("id") Long id) {
        Despesa despesa = em.find(Despesa.class, id);
        DespesaForm despesaForm = new DespesaForm();
        despesaForm.setId(despesa.getId());
        despesaForm.setCategoria(despesa.getCategoria());
        despesaForm.setData(despesa.getData());
        despesaForm.setDescricao(despesa.getDescricao());
        despesaForm.setValor(despesa.getValor());
        despesaForm.setLocalizacao(despesa.getLocalizacao());

        model.put("despesaForm", despesaForm);

        return "form";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(ModelMap model, @PathVariable("id") Long id) {

        Despesa despesa = em.find(Despesa.class, id);

        String data1 = despesa.getData();
        String[] parts = data1.split("-");
        int month1 = Integer.parseInt(parts[1]);

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month2 = localDate.getMonthValue();


        if(month1 == month2){
            em.remove(despesa);

            model.addAttribute("message", "Sucesso! A despesa de " + despesa.getCategoria() + " no dia " + despesa.getData() + " foi eliminada");
        } else {
            model.addAttribute("message", "Não pode remover despesas de meses anteriores.");
        }

        return "result";
    }

    @RequestMapping(value = "/mapa", method = RequestMethod.GET)
    public String getMap(ModelMap model){

        long id=1;
        float janTransportes, janAlimentacao, janPropinas, janRenda;
        janTransportes = janAlimentacao = janPropinas = janRenda = 0f;
        float fevTransportes, fevAlimentacao, fevPropinas, fevRenda;
        fevTransportes = fevAlimentacao = fevPropinas = fevRenda = 0f;
        float marTransportes, marAlimentacao, marPropinas, marRenda;
        marTransportes = marAlimentacao = marPropinas = marRenda = 0f;

        for (int i=0; i<id; i++) {

            Despesa despesa = em.find(Despesa.class, id);

            String data1 = despesa.getData();
            String[] parts = data1.split("-");
            int month = Integer.parseInt(parts[1]);

            if (month == 1) {
                if (Objects.equals(despesa.getCategoria(), "Tranportes")) {
                    janTransportes =+ despesa.getValor();
                    model.addAttribute("janTransportes", janTransportes);
                } else if (Objects.equals(despesa.getCategoria(), "Alimentação")) {
                    janAlimentacao =+ despesa.getValor();
                    model.addAttribute("janAlimentação", janAlimentacao);
                } else if (Objects.equals(despesa.getCategoria(), "Propinas")) {
                    janPropinas =+ despesa.getValor();
                    model.addAttribute("janPropinas", janPropinas);
                } else if (Objects.equals(despesa.getCategoria(), "Renda")) {
                    janRenda =+ despesa.getValor();
                    model.addAttribute("janRenda", janRenda);
                }
            }
            if (month == 2) {
                if (Objects.equals(despesa.getCategoria(), "Tranportes")) {
                    fevTransportes =+ despesa.getValor();
                    model.addAttribute("fevTransportes", fevTransportes);
                } else if (Objects.equals(despesa.getCategoria(), "Alimentação")) {
                    fevAlimentacao =+ despesa.getValor();
                    model.addAttribute("fevAlimentação", fevAlimentacao);
                } else if (Objects.equals(despesa.getCategoria(), "Propinas")) {
                    fevPropinas =+ despesa.getValor();
                    model.addAttribute("fevPropinas", fevPropinas);
                } else if (Objects.equals(despesa.getCategoria(), "Renda")) {
                    fevRenda =+ despesa.getValor();
                    model.addAttribute("fevRenda", fevRenda);
                }
            }
            if (month == 3) {
                if (Objects.equals(despesa.getCategoria(), "Tranportes")) {
                    marTransportes =+ despesa.getValor();
                    model.addAttribute("marTransportes", marTransportes);
                } else if (Objects.equals(despesa.getCategoria(), "Alimentação")) {
                    marAlimentacao =+ despesa.getValor();
                    model.addAttribute("marAlimentação", marAlimentacao);
                } else if (Objects.equals(despesa.getCategoria(), "Propinas")) {
                    marPropinas =+ despesa.getValor();
                    model.addAttribute("marPropinas", marPropinas);
                } else if (Objects.equals(despesa.getCategoria(), "Renda")) {
                    marRenda =+ despesa.getValor();
                    model.addAttribute("marRenda", marRenda);
                }
            }


            id++;
        }

        return "mapa";
    }
}

