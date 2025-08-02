package org.dows.gpt.prompt;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class PromptTemplate {
    public static int CN = 1;


    public static final String EXTRACT_PROMPT = """
            你是一个专业的文本内容语义分析和提取助手，专注于从各种文本或文档(txt,pdf,word,image)中提取以给定的元数据结构(json-schema)为基准的关键内容信息，并将其转成JSON结构化数据格式输出,严格按照按要求执行，不要胡乱增加没有的内容。
            如下示例:
            ###简历内容
            张三
            男 | 年龄：25岁 | 籍贯：北京 | 共产党员 | 18794434244
            求职意向：算法工程师 | 期望城市：北京
            个人优势
            擅长领域：深度学习、CV 、图像识别、语义分割、目标检测、自动驾驶感知算法、JavaWeb开发
            专业技能：熟悉 Python 、PyTorch 框架、熟悉 Java 、MySQL、SpringMVC、SpringBoot、Office
            教育经历
            北京工业大学 硕士 软件工程 2012-2015
            担任职务：党支部书记；主修课程：深度学习、图像识别、语义分割、遥感图像处理、软件工程、时空大数据
            河南工业大学 本科 计算机科学与技术 2008-2012
            担任职务：班长、党支部书记；主修课程：Java、数据库、操作系统、计算机网络、数据结构
            实习经历
            大模型自然语言处理科技（北京）有限公司 算法工程师 2023.12-2024.03
            ● 负责数据采集、清洗并标注2D、3D驾驶数据，确保数据质量和多样性
            ● 负责自动驾驶感知模块的算法开发与优化，利用行车影像数据进行模型迭代优化
            项目经历
            图像识别 总负责人 2022.09-至今
            ● 设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物
            总览可视化系统，实现对城市建筑物变化动态监测
            信息管理系统（SSM框架） 项目设计师 2024.02-2024.03
            负责高校党务信息管理系统的整体架构设计，确保系统功能模块化、高效稳定
            技术栈：Java、MySQL、MyBatis、HTML、CSS、JavaScript、Vue.js、AJAX、Spring MVC、Maven、Git
            ● 使用 MySQL 数据库设计，MyBatis 框架实现数据持久层的开发，提高 JDBC 开发效率
            ● 使用 HTML、CSS 和 JavaScript 技术， 结合 Element 组件库，快速构建响应式前端网页界面
            ###元数据结构(json-schema)
            {
                "title": "Resume",
                "type": "object",
                "properties": {
                    "fullName": {
                        "title": "全名",
                        "maxLength": 50,
                        "type": "string"
                    },
                    "contact": {
                        "title": "联系方式",
                        "type": "object",
                        "properties": {
                            "phone": {
                                "title": "电话号码",
                                "type": "string"
                            }
                        },
                        "required": ["phone"]
                    },
                    "education": {
                        "title": "教育背景",
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "degree": {
                                    "title": "学位",
                                    "type": "string"
                                },
                                "institution": {
                                    "title": "学校",
                                    "type": "string"
                                },
                                "fieldOfStudy": {
                                    "title": "专业",
                                    "type": "string"
                                },
                                "graduationYear": {
                                    "title": "毕业年份",
                                    "type": "integer"
                                }
                            },
                            "required": ["degree", "institution", "fieldOfStudy", "graduationYear"]
                        }
                    },
                    "experience": {
                        "title": "工作经验",
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "jobTitle": {
                                    "title": "职位",
                                    "type": "string"
                                },
                                "company": {
                                    "title": "公司",
                                    "type": "string"
                                },
                                "duration": {
                                    "title": "任职时间",
                                    "type": "string"
                                },
                                "responsibilities": {
                                    "title": "职责",
                                    "type": "array",
                                    "items": {
                                        "type": "string"
                                    }
                                }
                            },
                            "required": ["jobTitle", "company", "duration", "responsibilities"]
                        }
                    },
                    "skills": {
                        "title": "技能",
                        "type": "array",
                        "items": {
                            "type": "string"
                        }
                    }
                },
                "required": ["fullName", "contact", "education", "experience", "skills"]
            }
            ### 提取内容示例
            {
              "fullName": "张三",
              "contact": {
                "phone": "18794434244"
              },
              "education": [
                {
                  "degree": "硕士",
                  "institution": "北京工业大学",
                  "fieldOfStudy": "软件工程",
                  "graduationYear": 2015
                },
                {
                  "degree": "学士",
                  "institution": "河南工业大学",
                  "fieldOfStudy": "计算机科学与技术",
                  "graduationYear": 2012
                }
              ],
              "experience": [
                {
                  "jobTitle": "算法工程师",
                  "company": "大模型自然语言处理科技（北京）有限公司",
                  "duration": "2023.12-2024.03",
                  "responsibilities": [
                    "设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物",
                    "负责数据采集、清洗并标注2D、3D驾驶数据，确保数据质量和多样性",
                    "负责自动驾驶感知模块的算法开发与优化，利用行车影像数据进行模型迭代优化"
                  ]
                },
                {
                  "jobTitle": "图像识别",
                  "company": "图像识别",
                  "duration": "2022.09-至今",
                  "responsibilities": [
                    "设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物",
                    "实现对城市建筑物变化动态监测的建筑物",
                    "传感器数据采集停车场停车车位判断算法优化"
                  ]
                },
                {
                  "jobTitle": "信息管理系统（SSM框架）",
                  "company": "信息管理系统（SSM框架）",
                  "duration": "-non",
                  "responsibilities": [
                    "负责高校党务信息管理系统的整体架构设计，确保系统功能模块化、高效稳定",
                    "使用 MySQL 数据库设计，MyBatis 框架实现数据持久层的开发，提高 JDBC 开发效率",
                    "使用 HTML、CSS 和 JavaScript 技术， 结合 Element 组件库，快速构建响应式前端网页界面"
                  ]
                }
              ],
              "skills": [
                "Python",
                "PyTorch",
                "Java",
                "MySQL",
                "SpringMVC",
                "SpringBoot",
                "Office"
              ]
            }
            """;


    public static final String RESUME_EXAMPLE = """
            张三
            男 | 年龄：25岁 | 籍贯：北京 | 共产党员 | 18794434244
            求职意向：算法工程师 | 期望城市：北京
            个人优势
            擅长领域：深度学习、CV 、图像识别、语义分割、目标检测、自动驾驶感知算法、JavaWeb开发
            专业技能：熟悉 Python 、PyTorch 框架、熟悉 Java 、MySQL、SpringMVC、SpringBoot、Office
            教育经历
            北京工业大学 硕士 软件工程 2012-2015
            担任职务：党支部书记；主修课程：深度学习、图像识别、语义分割、遥感图像处理、软件工程、时空大数据
            河南工业大学 本科 计算机科学与技术 2008-2012
            担任职务：班长、党支部书记；主修课程：Java、数据库、操作系统、计算机网络、数据结构
            实习经历
            大模型自然语言处理科技（北京）有限公司 算法工程师 2023.12-2024.03
            ● 负责数据采集、清洗并标注2D、3D驾驶数据，确保数据质量和多样性
            ● 负责自动驾驶感知模块的算法开发与优化，利用行车影像数据进行模型迭代优化
            项目经历
            图像识别 总负责人 2022.09-至今
            ● 设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物
            总览可视化系统，实现对城市建筑物变化动态监测
            信息管理系统（SSM框架） 项目设计师 2024.02-2024.03
            负责高校党务信息管理系统的整体架构设计，确保系统功能模块化、高效稳定
            技术栈：Java、MySQL、MyBatis、HTML、CSS、JavaScript、Vue.js、AJAX、Spring MVC、Maven、Git
            ● 使用 MySQL 数据库设计，MyBatis 框架实现数据持久层的开发，提高 JDBC 开发效率
            ● 使用 HTML、CSS 和 JavaScript 技术， 结合 Element 组件库，快速构建响应式前端网页界面
            """;

    public static final String JSON_SCHEMA = """
            {
                "title": "Resume",
                "type": "object",
                "properties": {
                    "fullName": {
                        "title": "全名",
                        "maxLength": 50,
                        "type": "string"
                    },
                    "contact": {
                        "title": "联系方式",
                        "type": "object",
                        "properties": {
                            "phone": {
                                "title": "电话号码",
                                "type": "string"
                            }
                        },
                        "required": ["phone"]
                    },
                    "education": {
                        "title": "教育背景",
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "degree": {
                                    "title": "学位",
                                    "type": "string"
                                },
                                "institution": {
                                    "title": "学校",
                                    "type": "string"
                                },
                                "fieldOfStudy": {
                                    "title": "专业",
                                    "type": "string"
                                },
                                "graduationYear": {
                                    "title": "毕业年份",
                                    "type": "integer"
                                }
                            },
                            "required": ["degree", "institution", "fieldOfStudy", "graduationYear"]
                        }
                    },
                    "experience": {
                        "title": "工作经验",
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "jobTitle": {
                                    "title": "职位",
                                    "type": "string"
                                },
                                "company": {
                                    "title": "公司",
                                    "type": "string"
                                },
                                "duration": {
                                    "title": "任职时间",
                                    "type": "string"
                                },
                                "responsibilities": {
                                    "title": "职责",
                                    "type": "array",
                                    "items": {
                                        "type": "string"
                                    }
                                }
                            },
                            "required": ["jobTitle", "company", "duration", "responsibilities"]
                        }
                    },
                    "skills": {
                        "title": "技能",
                        "type": "array",
                        "items": {
                            "type": "string"
                        }
                    }
                },
                "required": ["fullName", "contact", "education", "experience", "skills"]
            }
            """;


    public static final String RESULT_EXAMPLE = """
            {
              "fullName": "张三",
              "contact": {
                "phone": "18794434244"
              },
              "education": [
                {
                  "degree": "硕士",
                  "institution": "北京工业大学",
                  "fieldOfStudy": "软件工程",
                  "graduationYear": 2015
                },
                {
                  "degree": "学士",
                  "institution": "河南工业大学",
                  "fieldOfStudy": "计算机科学与技术",
                  "graduationYear": 2012
                }
              ],
              "experience": [
                {
                  "jobTitle": "算法工程师",
                  "company": "大模型自然语言处理科技（北京）有限公司",
                  "duration": "2023.12-2024.03",
                  "responsibilities": [
                    "设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物",
                    "负责数据采集、清洗并标注2D、3D驾驶数据，确保数据质量和多样性",
                    "负责自动驾驶感知模块的算法开发与优化，利用行车影像数据进行模型迭代优化"
                  ]
                },
                {
                  "jobTitle": "图像识别",
                  "company": "图像识别",
                  "duration": "2022.09-至今",
                  "responsibilities": [
                    "设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物",
                    "实现对城市建筑物变化动态监测的建筑物",
                    "传感器数据采集停车场停车车位判断算法优化"
                  ]
                },
                {
                  "jobTitle": "信息管理系统（SSM框架）",
                  "company": "信息管理系统（SSM框架）",
                  "duration": "-non",
                  "responsibilities": [
                    "负责高校党务信息管理系统的整体架构设计，确保系统功能模块化、高效稳定",
                    "使用 MySQL 数据库设计，MyBatis 框架实现数据持久层的开发，提高 JDBC 开发效率",
                    "使用 HTML、CSS 和 JavaScript 技术， 结合 Element 组件库，快速构建响应式前端网页界面"
                  ]
                }
              ],
              "skills": [
                "Python",
                "PyTorch",
                "Java",
                "MySQL",
                "SpringMVC",
                "SpringBoot",
                "Office"
              ]
            }
            """;


    public static final String SYSTEM_PROMPT = """
            You are an expert ATS (Applicant Tracking System) analyzer. Your task is to:
            1. Evaluate the provided resume against standard ATS criteria or a specific job description if provided.
            2. Score the resume on a scale of 1-100 based on ATS compatibility.
            3. Provide specific feedback in the following categories:
               - Format and structure (20 points)
               - Keywords and relevance (30 points)
               - Quantifiable achievements (20 points)
               - Skills and qualifications (30 points)
            4. List specific strengths and weaknesses.
            5. Give actionable recommendations to improve the ATS score.
            
            规则1:
            
            {
                维度1:{
                    asdflsafj: 给给asdf
                    saldfjas: 请给出评估分值
                    维度1-1:{
                        sadlfja: dsfdsf
                        dsfsadf:  sdlf444
                    }
                }
                维度2:{
                    asdfdsf:asdfdsf
                    ldfasdlf:sdfdsf
                }
            }
            
            
            
            Return your analysis in a valid JSON format with the following structure:
            {
              "score": [overall score 1-100],
              "recommendation": "[brief overall recommendation]",
              "strengths": ["strength1", "strength2", ...],
              "weaknesses": ["weakness1", "weakness2", ...],
              "categoryScores": {
                "format": [score 1-20],
                "keywords": [score 1-30],
                "achievements": [score 1-20],
                "skills": [score 1-30]
                sdfdsf:{
                    aflfdsj:dsfjdslf
                    lasjf:jsadlfjdsaf
                    lsadjfldsaf:{
            
                    }
                }
              }
            }
            
            Make your evaluation comprehensive but concise.
            """;
    public static final String USER_PROMPT_WITHOUT_JD = """
            Please analyze this resume for ATS compatibility:
            
            {resumeText}
            """;
    public static final String USER_PROMPT_WITH_JD = """
            Please analyze this resume for ATS compatibility with the following job description:
            
            JOB DESCRIPTION:
            {jobDescription}
            
            RESUME:
            {resumeText}
            """;

    public static final String SYSTEM_PROMPT_CN = """
            您是一名专业的ATS（申请人追踪系统）分析专家。您的任务是:
            1. 根据标准ATS条件或提供的具体职位描述评估简历.
            2. 基于ATS兼容性按1-100分为简历评分.
            3. 在以下类别提供具体反馈:
               - 格式与结构 (20 points)
               - 关键词与相关性 (30 points)
               - 可量化成果 (20 points)
               - 技能与资质证书 (30 points)
            4. 列出具体优势与不足.
            5. 提供可操作的改进建议以提高ATS分数.
            6. 项目经验包括：名称，描述，角色，技术栈，开始时间，结束时间，公司，按JSON结构列表放到项目经验位置.
            7. 优势及亮点每项不超过7个字.
            8. 推荐关联词不超过30个字.
            
            Return your analysis in a valid JSON format with the following structure:
            {
              "评分": [整体评分 1-100],
              "推荐关联词": "[brief overall recommendation]",
              "优势及亮点": ["strength1", "strength2", ...],
              "弱势": ["weakness1", "weakness2", ...
              "基础数据": ${dataItems}],
              "每项评分": ${scoreItems}
              }
            }
            
            请做出详尽而精炼的评估.
            """;
    public static final String USER_PROMPT_WITHOUT_JD_CN = """
            请分析这份简历的ATS系统兼容性:
            
            简历内容
            ${resumeText}
            """;
    public static final String USER_PROMPT_WITH_JD_CN = """
            请分析这份简历与以下职位描述的ATS系统兼容性:
            
            岗位描述:
            ${jobDescription}
            
            简历内容:
            ${resumeText}
            """;

    public static String getSystemPrompt(Map<String, String> values, int cnFlag) {
        if (cnFlag == 1) {
            return StringSubstitutor.replace(SYSTEM_PROMPT_CN, values);
        } else {
            return StringSubstitutor.replace(SYSTEM_PROMPT, values);
        }
    }

    public static String getUserPromptWithoutJd(Map<String, String> values, int cnFlag) {
        if (cnFlag == 1) {
            return StringSubstitutor.replace(USER_PROMPT_WITHOUT_JD_CN, values);
        } else {
            return StringSubstitutor.replace(USER_PROMPT_WITHOUT_JD, values);
        }
    }

    public static String getUserPromptWithJd(Map<String, String> values, int cnFlag) {
        if (cnFlag == 1) {
            return StringSubstitutor.replace(USER_PROMPT_WITH_JD_CN, values);
        } else {
            return StringSubstitutor.replace(USER_PROMPT_WITH_JD, values);
        }
    }
}
