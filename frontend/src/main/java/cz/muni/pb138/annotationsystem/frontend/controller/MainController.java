package cz.muni.pb138.annotationsystem.frontend.controller;

import au.com.bytecode.opencsv.CSVReader;
import cz.muni.pb138.annotationsystem.backend.api.*;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


@Controller
@RequestMapping("/**")
public class MainController {

    @Inject
    private PackManager packManager;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private EvaluationManager evaluationManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private StatisticsManager statisticsManager;

    @Inject
    private SubpackManager subpackManager;

    @RequestMapping("/")
    public String primaryView(ServletRequest req, HttpServletRequest httpReq) {

        try {
            req.setAttribute("person", personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser()));
        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-admin";

    }

    @RequestMapping(value = "/upload", method = {RequestMethod.GET})
    public String doGet(ServletRequest req) {

        return "view-upload";
    }

    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public String doPost(RedirectAttributes redirectAttributes,
                         ServletRequest req,
                         @RequestParam("file") MultipartFile[] files,
                         @RequestParam("value") String[] values) {

        if (!(files.length < 1)) {

            List<String> answersList = new ArrayList<String>();
            String[] nextLineAnswers;

            List<String> noiseList = new ArrayList<String>();
            String[] nextLineNoise;

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(files[0].getInputStream()));
                CSVReader reader = new CSVReader(bufferedReader);

                while ((nextLineAnswers = reader.readNext()) != null) {
                    for (int i = 0; i < nextLineAnswers.length; i++) {
                        if (!nextLineAnswers[i].isEmpty()) answersList.add(nextLineAnswers[i]);
                    }
                }
            } catch (IOException e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            if (files.length > 1) {

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(files[1].getInputStream()));
                    CSVReader reader = new CSVReader(bufferedReader);

                    while ((nextLineNoise = reader.readNext()) != null) {
                        for (int i = 0; i < nextLineNoise.length; i++) {
                            if (!nextLineNoise[i].isEmpty()) noiseList.add(nextLineNoise[i]);
                        }
                    }
                } catch (IOException e) {
                    req.setAttribute("error", e);
                    return "view-error";
                }
            }

            Pack pack = new Pack(answersList.get(0), files[0].getOriginalFilename(), Double.parseDouble(values[0]), Double.parseDouble(values[1]));

            List<String> helpList = new ArrayList<String>();

            for (int i = 2; i < answersList.size(); i++) {
                helpList.add(answersList.get(i));
            }

            try {
                packManager.createPack(pack, helpList, noiseList, Integer.parseInt(values[2]));

                //implicitne priradenie packu Karlikovi pre moznost testovania, lebo..
                subpackManager.updatePersonsAssignment(personManager.getOrCreatePersonByUsername("Karlik"), subpackManager.getSubpacksInPack(pack));

            } catch (Exception e) {
                req.setAttribute("error", e);
                return "view-error";
            }

            //System.out.println("AAAAAAAAAAAAAAAAAAAAAA" + pack + ".." + values[0] + ".." + values[1] );
            //System.out.println("BBBBBBBBBBBBBBBBBBBBBB" + pack + ".." + answersList + ".." + noiseList + ".." + values[2] );

            redirectAttributes.addFlashAttribute("pack", pack);

            return "redirect:/assign/" + pack.getId();
        } else {
            req.setAttribute("error", "File empty.");
            return "view-error";
        }

    }

    @RequestMapping("/packages")
    public String packages(ServletRequest req, HttpServletRequest httpReq) {

        try {
            req.setAttribute("subpacks", subpackManager.getSubpacksAssignedToPerson(personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser())));

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-packages";

    }

    @RequestMapping(value = "/packages/{subpack}", method = {RequestMethod.GET})
    public String getPackage(RedirectAttributes redirectAttributes, ServletRequest req, @PathVariable String subpack) {

        try {
            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "redirect:/mark/{subpack}";
    }

    @RequestMapping("/stats")
    public String stats(ServletRequest req) {

        List<Pack> allPacks = null;
        try {
            allPacks = packManager.getAllPacks();
        } catch (DaoException e) {
            e.printStackTrace();
        }
        req.setAttribute("allPacks", allPacks);

        return "view-stats";
    }

    @RequestMapping("/stats/{pack}")
    public String stats(ServletRequest req, @PathVariable String pack) {
        try {
            Long longPack = Long.parseLong(pack);
            Pack packObj = packManager.getPackById(longPack);
            double width = statisticsManager.getProgressOfPack(packObj);
            req.setAttribute("Pack", packObj);
            req.setAttribute("width", width);
            List<Subpack> allSubPacks = subpackManager.getSubpacksInPack(packObj);
            req.setAttribute("allSubPacks", allSubPacks);
            Map statMap = new HashMap();

            for (Subpack subPack : allSubPacks) {
                List<Person> userList = subpackManager.getPersonsAssignedToSubpack(subPack);
                Map subpackStat = new HashMap();
                for (Person user : userList) {
                    subpackStat.put(user, statisticsManager.getProgressOfSubpackForPerson(subPack, user));
                }
                statMap.put(subPack, subpackStat);
            }
            req.setAttribute("stats", statMap);

        } catch (Exception e) {
            return "redirect:/view-error";
        }
        return "view-statsPack";
    }

    @RequestMapping(value = "/mark/{subpack}", method = {RequestMethod.GET})
    public String markGet(ServletRequest req, @PathVariable String subpack, HttpServletRequest httpReq) {

        try {

            req.setAttribute("lStartTime", System.currentTimeMillis());

            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            req.setAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            req.setAttribute("thisQuestion", question);

            try {
                Answer answer = answerManager.nextAnswer(personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser()),
                        subpackManager.getSubpackById(Long.parseLong(subpack)));
                req.setAttribute("thisAnswer", answer);
            } catch (IllegalStateException e) {
                if (e.getMessage() == "No more answers left.") {
                    return "view-finished";
                } else {
                    return "redirect:/view-error";
                }
            }

        } catch (Exception e) {
            return "redirect:/view-error";
        }
        return "view-mark";

    }

    @RequestMapping(value = "/mark/{subpack}/{answer}/{time}", method = {RequestMethod.POST})
    public String markPost(RedirectAttributes redirectAttributes, @RequestParam String value,
                           @PathVariable String subpack, @PathVariable String answer,
                           @PathVariable String time, HttpServletRequest httpReq) {

        try {

            long lEndTime = System.currentTimeMillis();

            long difference = lEndTime - Long.parseLong(time);

            Answer thisAnswer = answerManager.getAnswerById(Long.parseLong(answer));
            Person thisPerson = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());

            if (Integer.parseInt(value) == 1) {
                Evaluation evaluation = new Evaluation(thisPerson, thisAnswer, Rating.POSITIVE, (int) (long) difference);
                evaluationManager.eval(evaluation);
            } else {
                Evaluation evaluation = new Evaluation(thisPerson, thisAnswer, Rating.NEGATIVE, (int) (long) difference);
                evaluationManager.eval(evaluation);
            }

            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            redirectAttributes.addFlashAttribute("thisQuestion", question);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "redirect:/mark/{subpack}";

    }

    @RequestMapping(value = "/mark/{subpack}/{answer}/report/{time}", method = {RequestMethod.POST})
    public String markReport(RedirectAttributes redirectAttributes, @PathVariable String time,
                             @PathVariable String subpack, @PathVariable String answer, HttpServletRequest httpReq) {

        try {

            long lEndTime = System.currentTimeMillis();

            long difference = lEndTime - Long.parseLong(time);

            Answer thisAnswer = answerManager.getAnswerById(Long.parseLong(answer));
            Person thisPerson = personManager.getOrCreatePersonByUsername(httpReq.getRemoteUser());

            Evaluation evaluation = new Evaluation(thisPerson, thisAnswer, Rating.NONSENSE, (int) (long) difference);
            evaluationManager.eval(evaluation);

            Long longSubpack = Long.parseLong(subpack);
            Subpack thisSubpack = subpackManager.getSubpackById(longSubpack);
            redirectAttributes.addFlashAttribute("thisSubpack", thisSubpack);

            Pack pack = thisSubpack.getParent();
            String question = pack.getQuestion();
            redirectAttributes.addFlashAttribute("thisQuestion", question);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "redirect:/mark/{subpack}";

    }

    @RequestMapping(value = "/assign/{packID}/{userID}", method = {RequestMethod.GET})
    public String assignPackGet(ServletRequest req, @PathVariable String packID, @PathVariable String userID) {

        try {
            long longPackID = Long.parseLong(packID);
            long longUserID = Long.parseLong(userID);

            Pack pack = packManager.getPackById(longPackID);
            List<Subpack> subpackList = subpackManager.getSubpacksInPack(pack);
            Person user = personManager.getPersonById(longUserID);

            req.setAttribute("user", user);
            req.setAttribute("subPackList", subpackList);
            req.setAttribute("pack", pack);
        } catch (DaoException e) {
            return "redirect:/view-error";
        }
        return "view-assignPack";
    }


    @RequestMapping("/assign/{id}")
    public String assign(ServletRequest req, @PathVariable String id) {
        try {
            long packID = Long.parseLong(id);
            Pack pack = packManager.getPackById(packID);
            List<Person> users = personManager.getAllPersons();
            req.setAttribute("users", users);
            req.setAttribute("pack", pack);

        } catch (DaoException e) {
            return "redirect:/view-error";
        }

        return "view-assign";

    }


}
