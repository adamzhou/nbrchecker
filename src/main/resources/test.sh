#!/bin/bash
#

if [ "a$1" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$2" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$3" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

if [ "a$4" == "a" ]
then
    echo "Please input arguments: test.sh <work dir> <idx file path> <dat file path> <output file prefix>"
    exit
fi

echo $1
echo $2
echo $3
echo $4

WORK_DIR=$1
IDX_FILE=$2
DAT_FILE=$3
OUTPUT_PREFIX=$4
EXECUTOR=${WORK_DIR}"/nbrtool"
CMD=${EXECUTOR}" v "${IDX_FILE}" "${DAT_FILE}" "${OUTPUT_PREFIX}

date
export LD_LIBRARY_PATH=$WORK_DIR
export DISPLAY=:0.0
export SHELL=/bin/bash
export LS_COLORS=rs=0:di=01;34:ln=01;36:mh=00:pi=40;33:so=01;35:do=01;35:bd=40;33;01:cd=40;33;01:or=40;31;01:mi=01;05;37;41:su=37;41:sg=30;43:ca=30;41:tw=30;42:ow=34;42:st=37;44:ex=01;32:*.tar=01;31:*.tgz=01;31:*.arj=01;31:*.taz=01;31:*.lzh=01;31:*.lzma=01;31:*.tlz=01;31:*.txz=01;31:*.zip=01;31:*.z=01;31:*.Z=01;31:*.dz=01;31:*.gz=01;31:*.lz=01;31:*.xz=01;31:*.bz2=01;31:*.tbz=01;31:*.tbz2=01;31:*.bz=01;31:*.tz=01;31:*.deb=01;31:*.rpm=01;31:*.jar=01;31:*.rar=01;31:*.ace=01;31:*.zoo=01;31:*.cpio=01;31:*.7z=01;31:*.rz=01;31:*.jpg=01;35:*.jpeg=01;35:*.gif=01;35:*.bmp=01;35:*.pbm=01;35:*.pgm=01;35:*.ppm=01;35:*.tga=01;35:*.xbm=01;35:*.xpm=01;35:*.tif=01;35:*.tiff=01;35:*.png=01;35:*.svg=01;35:*.svgz=01;35:*.mng=01;35:*.pcx=01;35:*.mov=01;35:*.mpg=01;35:*.mpeg=01;35:*.m2v=01;35:*.mkv=01;35:*.ogm=01;35:*.mp4=01;35:*.m4v=01;35:*.mp4v=01;35:*.vob=01;35:*.qt=01;35:*.nuv=01;35:*.wmv=01;35:*.asf=01;35:*.rm=01;35:*.rmvb=01;35:*.flc=01;35:*.avi=01;35:*.fli=01;35:*.flv=01;35:*.gl=01;35:*.dl=01;35:*.xcf=01;35:*.xwd=01;35:*.yuv=01;35:*.cgm=01;35:*.emf=01;35:*.axv=01;35:*.anx=01;35:*.ogv=01;35:*.ogx=01;35:*.aac=01;36:*.au=01;36:*.flac=01;36:*.mid=01;36:*.midi=01;36:*.mka=01;36:*.mp3=01;36:*.mpc=01;36:*.ogg=01;36:*.ra=01;36:*.wav=01;36:*.axa=01;36:*.oga=01;36:*.spx=01;36:*.xspf=01;36:
export PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/usr/lib/jvm/jre/bin:/root/bin
export INPUTRC=/etc/inputrc
export LANG=en_US.UTF-8
export SHLVL=1
export CLASSPATH=.:/usr/lib/jvm/jre/lib
export LESSOPEN=||/usr/bin/lesspipe.sh %s
export G_BROKEN_FILENAMES=1
export _=/bin/env
echo $CMD
eval $CMD
date
exit

