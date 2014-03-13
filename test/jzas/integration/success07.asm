mac: macro

rot:	ld a, 0
	jp rot
	endm

init:	mac
	end init
