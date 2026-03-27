package com.top.talent.management.utils;

import com.top.talent.management.entity.Category;
import com.top.talent.management.entity.EmailCategories;
import com.top.talent.management.entity.FutureSkillCategory;
import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.repository.CategoryRepository;
import com.top.talent.management.repository.EmailCategoriesRepository;
import com.top.talent.management.repository.FutureSkillCategoryRepository;
import com.top.talent.management.repository.PracticeDelegationFeatureRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.top.talent.management.constants.Constants.SYSTEM;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialSetupUtils implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;
    private final FutureSkillCategoryRepository futureSkillCategoryRepository;
    private final EmailCategoriesRepository emailCategoriesRepository;

    @Override
    public void run(String... args) {
        checkAndSetupInitialDatabase();
    }
    private void checkAndSetupInitialDatabase() {
        checkAndSetupInitialUserRoles();

        checkAndInitialStepCategories();
        checkAndInitialStepSubCategories();

        checkAndSetupInitialPracticeDelegationFeatures();

        checkAndSetUpInitialFutureSkillQuestions();
        checkAndSetupInitialEmailCategories();
    }

    private void checkAndSetupInitialEmailCategories() {
        if (emailCategoriesRepository.findAll().isEmpty()) {
            emailCategoriesRepository.saveAll(getEmailCategories());
        }
    }

    private void checkAndSetupInitialUserRoles() {
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.saveAll(getRolesList());
        }
    }

    private void checkAndInitialStepCategories() {
        if (categoryRepository.findAll().isEmpty()) {
            categoryRepository.saveAll(getPracticeCategories());
        }
    }

    private void checkAndInitialStepSubCategories() {
        if (subCategoryRepository.findAll().isEmpty()) {
            subCategoryRepository.saveAll(getPracticeSubCategories());
        }
    }

    private void checkAndSetupInitialPracticeDelegationFeatures() {
        if (practiceDelegationFeatureRepository.findAll().isEmpty()) {
            practiceDelegationFeatureRepository.saveAll(getPracticeDelegationFeatureList());
        }
    }




    private void checkAndSetUpInitialFutureSkillQuestions() {
        if (futureSkillCategoryRepository.findAll().isEmpty()) {
            List<FutureSkillCategory> predefinedCategories = getPredefinedQuestions();
            futureSkillCategoryRepository.saveAll(predefinedCategories);
        }

    }
    private List<Role> getRolesList() {
        Role roleSU = Role.builder()
                .id(1L)
                .name("SA")
                .build();
        roleSU.setCreatedByAndUpdatedBy(SYSTEM);

        Role roleSA = Role.builder()
                .id(2L)
                .name("SU")
                .build();
        roleSA.setCreatedByAndUpdatedBy(SYSTEM);

        Role roleU = Role.builder()
                .id(3L)
                .name("U")
                .build();
        roleU.setCreatedByAndUpdatedBy(SYSTEM);

        Role roleP = Role.builder()
                .id(4L)
                .name("P")
                .build();
        roleP.setCreatedByAndUpdatedBy(SYSTEM);

        Role roleHRBP = Role.builder()
                .id(5L)
                .name("HRBP")
                .build();
        roleHRBP.setCreatedByAndUpdatedBy(SYSTEM);


        return List.of(roleSU, roleSA, roleU, roleP, roleHRBP);
    }

    private List<Category> getPracticeCategories() {
        Category abilityCategory = Category.builder()
                .categoryId(1L)
                .categoryName("Ability")
                .build();
        abilityCategory.setCreatedByAndUpdatedBy(SYSTEM);

        Category aspirationCategory = Category.builder()
                .categoryId(2L)
                .categoryName("Aspiration")
                .build();
        aspirationCategory.setCreatedByAndUpdatedBy(SYSTEM);

        Category engagementCategory = Category.builder()
                .categoryId(3L)
                .categoryName("Engagement")
                .build();
        engagementCategory.setCreatedByAndUpdatedBy(SYSTEM);


        return List.of(abilityCategory, aspirationCategory, engagementCategory);
    }

    private List<SubCategory> getPracticeSubCategories() {
        List<SubCategory> abilitySubCategories = getPracticeSubCategoriesForAbility();
        List<SubCategory> aspirationSubCategories = getPracticeSubCategoriesForAspiration();
        List<SubCategory> engagementSubCategories = getPracticeSubCategoriesForEngagement();

        return Stream.of(abilitySubCategories, aspirationSubCategories, engagementSubCategories)
                .flatMap(List::stream)
                .toList();
    }

    private List<SubCategory> getPracticeSubCategoriesForAbility() {
        SubCategory subCategory1 = SubCategory.builder()
                .subCategoryName("Strategic Orientation")
                .description("Does the associate have the capacity to engage in broad, complex analytical, and conceptual thinking to identify growth opportunities/ future?")
                .category(Category.builder().categoryId(1L).build())
                .build();
        subCategory1.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory2 = SubCategory.builder()
                .subCategoryName("Influence")
                .description("Is the associate capable of working effectively with peers or partners, influencing stakeholders across organizational boundaries for a win-win?")
                .category(Category.builder().categoryId(1L).build())
                .build();
        subCategory2.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory3 = SubCategory.builder()
                .subCategoryName("Result Orientation")
                .description("Is the associate committed to drive results across complex, diverse projects taking complete ownership?")
                .category(Category.builder().categoryId(1L).build())
                .build();
        subCategory3.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory4 = SubCategory.builder()
                .subCategoryName("Communication")
                .description("Does the associate set strategic direction for teams and effectively communicates that strategy up, down and across the organization?")
                .category(Category.builder().categoryId(1L).build())
                .build();
        subCategory4.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory5 = SubCategory.builder()
                .subCategoryName("Decision Making")
                .description("Does the associate have the ability to make quick, accurate, complex, even unpopular decisions taking into account wider implications on people, strategy and resources?")
                .category(Category.builder().categoryId(1L).build())
                .build();
        subCategory5.setCreatedByAndUpdatedBy(SYSTEM);

        return List.of(subCategory1, subCategory2, subCategory3, subCategory4, subCategory5);
    }

    private List<SubCategory> getPracticeSubCategoriesForAspiration() {
        SubCategory subCategory1 = SubCategory.builder()
                .subCategoryName("Ask for More")
                .description("Does the associate communicate a desire to assume higher responsibility with increased complexity/ scale?")
                .category(Category.builder().categoryId(2L).build())
                .build();
        subCategory1.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory2 = SubCategory.builder()
                .subCategoryName("Agility")
                .description("Is the associate able to adapt to change, manage difficult and ambiguous problems with confidence, resilience, and resourcefulness?")
                .category(Category.builder().categoryId(2L).build())
                .build();
        subCategory2.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory3 = SubCategory.builder()
                .subCategoryName("Visibility")
                .description("Is the associate driven by higher visibility and recognition?")
                .category(Category.builder().categoryId(2L).build())
                .build();
        subCategory3.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory4 = SubCategory.builder()
                .subCategoryName("Feedback")
                .description("Does the associate seek and apply feedback while taking risks to gain new experiences and build key capabilities?")
                .category(Category.builder().categoryId(2L).build())
                .build();
        subCategory4.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory5 = SubCategory.builder()
                .subCategoryName("Initiative/ New & Different")
                .description("Does the associate display initiatives to identify forward looking technologies and take on responsibilities even outside his or her role?")
                .category(Category.builder().categoryId(2L).build())
                .build();
        subCategory5.setCreatedByAndUpdatedBy(SYSTEM);

        return List.of(subCategory1, subCategory2, subCategory3, subCategory4, subCategory5);
    }

    private List<SubCategory> getPracticeSubCategoriesForEngagement() {
        SubCategory subCategory1 = SubCategory.builder()
                .subCategoryName("Developing Org Capabilities")
                .description("Does the associate actively develop, coaches, and mentors team to unlock their potential?")
                .category(Category.builder().categoryId(3L).build())
                .build();
        subCategory1.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory2 = SubCategory.builder()
                .subCategoryName("Stay")
                .description("Does the associate see himself working for EPAM for a longer time?")
                .category(Category.builder().categoryId(3L).build())
                .build();
        subCategory2.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory3 = SubCategory.builder()
                .subCategoryName("Personal Connect")
                .description("Is the associate personally connected to the success of the organization and his/her team?")
                .category(Category.builder().categoryId(3L).build())
                .build();
        subCategory3.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory4 = SubCategory.builder()
                .subCategoryName("Excellence")
                .description("Does the associate set a higher bar for excellence and motivate his/her team to meet expectations?")
                .category(Category.builder().categoryId(3L).build())
                .build();
        subCategory4.setCreatedByAndUpdatedBy(SYSTEM);

        SubCategory subCategory5 = SubCategory.builder()
                .subCategoryName("Say")
                .description("Is the associate a brand ambassador of EPAM and communicates positive brand image internally and externally?")
                .category(Category.builder().categoryId(3L).build())
                .build();
        subCategory5.setCreatedByAndUpdatedBy(SYSTEM);

        return List.of(subCategory1, subCategory2, subCategory3, subCategory4, subCategory5);
    }

    private List<PracticeDelegationFeature> getPracticeDelegationFeatureList() {
        PracticeDelegationFeature practiceRatingFeature = PracticeDelegationFeature.builder()
                .name("Practice Rating")
                .frontendPath("/practice")
                .build();
        practiceRatingFeature.setCreatedByAndUpdatedBy(SYSTEM);

        PracticeDelegationFeature futureSkillsFeature = PracticeDelegationFeature.builder()
                .name("Future Skills")
                .frontendPath("/future_skills")
                .build();
        futureSkillsFeature.setCreatedByAndUpdatedBy(SYSTEM);

        return List.of(practiceRatingFeature, futureSkillsFeature);
    }

    public List<FutureSkillCategory> getPredefinedQuestions() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                FutureSkillCategory.builder()
                        .categoryName("Critical Behavioral Competencies")
                        .questions(List.of(
                                "Which leadership or interpersonal skills are most critical for your practice’s success?",
                                "How important are collaboration, communication, and team-building in your domain?",
                                "What emerging trends (technological, market, regulatory) will shape your practice in the next 2–3 years?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Industry & Market Trends")
                        .questions(List.of(
                                "How do these trends influence the skills needed by our top talent?",
                                "Do you see a need for client-facing or consulting skills to improve project outcomes?",
                                "Which competitor moves or market shifts must we address to stay competitive?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Business & Strategic Alignment")
                        .questions(List.of(
                                "Which new business goals or service lines is EPAM India targeting within your domain?",
                                "How can these future skills support those goals?",
                                "Are there any client requests or RFPs indicating the need for new capabilities?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Aspirational Capabilities")
                        .questions(List.of(
                                "Which skills are missing or underrepresented in your practice today?",
                                "Where do we see bottlenecks if these skills aren’t developed?",
                                "Are there any advanced/specialized skill sets that could accelerate growth if addressed now?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Participants New Roles & Responsibilities")
                        .questions(List.of(
                                "Which key roles (technical or leadership) will be most critical over the next 2–3 years?",
                                "What new responsibilities will emerge or expand in these roles?",
                                "How can these responsibilities be translated into specific competencies?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Technology & Domain Focus")
                        .questions(List.of(
                                "Which technologies, tools, or platforms will be essential in the next 2–3 years?",
                                "Do we need domain-specific knowledge (e.g., certifications, compliance) to stay competitive?",
                                "How rapidly are these technologies evolving, and how do we keep pace?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Commitment to Capability Building (Training, Mentoring, Certifications, etc.)")
                        .questions(List.of(
                                "Are there external trainings, certifications, or partnerships we should consider?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Prioritization & Impact")
                        .questions(List.of(
                                "Which skills are must-have (top priority) vs. nice-to-have?",
                                "How would you rank them by potential business impact?",
                                "Are there any quick-win skills that can deliver immediate results while we work on more complex capabilities?"
                        ))
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Future Skills (3–5 years)")
                        .questions(List.of())
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Expected Time to Readiness")
                        .questions(List.of())
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Opportunities & Business Problems Solved")
                        .questions(List.of())
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build(),
                FutureSkillCategory.builder()
                        .categoryName("Estimated Budget Requirement Per person")
                        .questions(List.of())
                        .createdBy(SYSTEM)
                        .lastUpdatedBy(SYSTEM)
                        .created(now)
                        .lastUpdated(now)
                        .build()
        );
    }
    private List<EmailCategories> getEmailCategories() {
        return List.of(
                createEmailCategory("Future Skill", "Reminder mails for Future Skill"),
                createEmailCategory("Practice Rating", "Reminder mails for Practice Rating")
        );
    }

    private EmailCategories createEmailCategory(String name, String description) {
        return EmailCategories.builder()
                .name(name)
                .description(description)
                .createdBy(SYSTEM)
                .lastUpdatedBy(SYSTEM)
                .created(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
