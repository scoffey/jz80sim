		aseg
		org 3000h

calculaDirecc:	push AF
		push BC
		dec C
		mlt BC
		add IX, BC
		pop BC
		pop AF
		ret

start:		call calculaDirecc
		rst 38h
		end start
