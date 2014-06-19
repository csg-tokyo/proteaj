#/bin/zsh

script_dir="/home/ichikawa/Documents/research/experiment/proteaj/proteaj"

java -classpath ${script_dir}/lib/javassist.jar:${script_dir}/out/production/proteaj:${script_dir}/bin:lib/geronimo-jms_1.1_spec-1.0.jar:lib/mail-1.4.3.jar proteaj.Compiler $@
