init	ld a, 0
r1:	db 0
r2:	db 0
	ld (1+r1-r2), a
	end init
