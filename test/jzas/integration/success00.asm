	ld A, (BC)
	ld A, (DE)
	ld (BC), A
	ld (DE), A
	ld HL, (08fdH)
	ld HL, (0dcfeH)
	ld A, (08fdH)
	ld A, (0dcfeH)
	ld (08fdH), HL
	ld (0dcfeH), HL
	ld (08fdH), A
	ld (0dcfeH), A
	ld B, B
	ld B, C
	ld B, D
	ld B, E
	ld B, H
	ld B, L
	ld B, (HL)
	ld B, A
	ld C, B
	ld C, C
	ld C, D
	ld C, E
	ld C, H
	ld C, L
	ld C, (HL)
	ld C, A
	ld D, B
	ld D, C
	ld D, D
	ld D, E
	ld D, H
	ld D, L
	ld D, (HL)
	ld D, A
	ld E, B
	ld E, C
	ld E, D
	ld E, E
	ld E, H
	ld E, L
	ld E, (HL)
	ld E, A
	ld H, B
	ld H, C
	ld H, D
	ld H, E
	ld H, H
	ld H, L
	ld H, (HL)
	ld H, A
	ld L, B
	ld L, C
	ld L, D
	ld L, E
	ld L, H
	ld L, L
	ld L, (HL)
	ld L, A
	ld (HL), B
	ld (HL), C
	ld (HL), D
	ld (HL), E
	ld (HL), H
	ld (HL), L
	halt
	ld (HL), A
	ld A, B
	ld A, C
	ld A, D
	ld A, E
	ld A, H
	ld A, L
	ld A, (HL)
	ld A, A
	ld B, 01
	ld B, 0feH
	ld C, 01
	ld C, 0feH
	ld D, 01
	ld D, 0feH
	ld E, 01
	ld E, 0feH
	ld H, 01
	ld H, 0feH
	ld L, 01
	ld L, 0feH
	ld (HL), 01
	ld (HL), 0feH
	ld A, 01
	ld A, 0feH
	ld SP, HL
	ld BC, (08fdH)
	ld BC, (0dcfeH)
	ld DE, (08fdH)
	ld DE, (0dcfeH)
	ld HL, (08fdH)
	ld HL, (0dcfeH)
	ld SP, (08fdH)
	ld SP, (0dcfeH)
	ld (08fdH), BC
	ld (0dcfeH), BC
	ld (08fdH), DE
	ld (0dcfeH), DE
	ld (08fdH), HL
	ld (0dcfeH), HL
	ld (08fdH), SP
	ld (0dcfeH), SP
	ld BC, 08fdH
	ld BC, 0dcfeH
	ld DE, 08fdH
	ld DE, 0dcfeH
	ld HL, 08fdH
	ld HL, 0dcfeH
	ld SP, 08fdH
	ld SP, 0dcfeH
	ld A, I
	ld A, R
	ld I, A
	ld R, A
	ex AF, AF
	ex (SP), HL
	ex DE, HL
	exx
	di
	ei
	cpl
	and B
	and C
	and D
	and E
	and H
	and L
	and (HL)
	and A
	and 01
	and 0feH
	or B
	or C
	or D
	or E
	or H
	or L
	or (HL)
	or A
	or 01
	or 0feH
	xor B
	xor C
	xor D
	xor E
	xor H
	xor L
	xor (HL)
	xor A
	xor 01
	xor 0feH
	neg
	daa
	dec B
	dec C
	dec D
	dec E
	dec H
	dec L
	dec (HL)
	dec A
	dec BC
	dec DE
	dec HL
	dec SP
	inc B
	inc C
	inc D
	inc E
	inc H
	inc L
	inc (HL)
	inc A
	inc BC
	inc DE
	inc HL
	inc SP
	mlt BC
	mlt DE
	mlt HL
	mlt SP
	sbc A, B
	sbc A, C
	sbc A, D
	sbc A, E
	sbc A, H
	sbc A, L
	sbc A, (HL)
	sbc A, A
	sbc A, 01
	sbc A, 0feH
	sbc HL, BC
	sbc HL, DE
	sbc HL, HL
	sbc HL, SP
	sub B
	sub C
	sub D
	sub E
	sub H
	sub L
	sub (HL)
	sub A
	sub 01
	sub 0feH
	adc A, B
	adc A, C
	adc A, D
	adc A, E
	adc A, H
	adc A, L
	adc A, (HL)
	adc A, A
	adc A, 01
	adc A, 0feH
	adc HL, BC
	adc HL, DE
	adc HL, HL
	adc HL, SP
	add A, B
	add A, C
	add A, D
	add A, E
	add A, H
	add A, L
	add A, (HL)
	add A, A
	add A, 01
	add A, 0feH
	add HL, BC
	add HL, DE
	add HL, HL
	add HL, SP
	res 0, B
	res 0, C
	res 0, D
	res 0, E
	res 0, H
	res 0, L
	res 0, (HL)
	res 0, A
	res 1, B
	res 1, C
	res 1, D
	res 1, E
	res 1, H
	res 1, L
	res 1, (HL)
	res 1, A
	res 2, B
	res 2, C
	res 2, D
	res 2, E
	res 2, H
	res 2, L
	res 2, (HL)
	res 2, A
	res 3, B
	res 3, C
	res 3, D
	res 3, E
	res 3, H
	res 3, L
	res 3, (HL)
	res 3, A
	res 4, B
	res 4, C
	res 4, D
	res 4, E
	res 4, H
	res 4, L
	res 4, (HL)
	res 4, A
	res 5, B
	res 5, C
	res 5, D
	res 5, E
	res 5, H
	res 5, L
	res 5, (HL)
	res 5, A
	res 6, B
	res 6, C
	res 6, D
	res 6, E
	res 6, H
	res 6, L
	res 6, (HL)
	res 6, A
	res 7, B
	res 7, C
	res 7, D
	res 7, E
	res 7, H
	res 7, L
	res 7, (HL)
	res 7, A
	set 0, B
	set 0, C
	set 0, D
	set 0, E
	set 0, H
	set 0, L
	set 0, (HL)
	set 0, A
	set 1, B
	set 1, C
	set 1, D
	set 1, E
	set 1, H
	set 1, L
	set 1, (HL)
	set 1, A
	set 2, B
	set 2, C
	set 2, D
	set 2, E
	set 2, H
	set 2, L
	set 2, (HL)
	set 2, A
	set 3, B
	set 3, C
	set 3, D
	set 3, E
	set 3, H
	set 3, L
	set 3, (HL)
	set 3, A
	set 4, B
	set 4, C
	set 4, D
	set 4, E
	set 4, H
	set 4, L
	set 4, (HL)
	set 4, A
	set 5, B
	set 5, C
	set 5, D
	set 5, E
	set 5, H
	set 5, L
	set 5, (HL)
	set 5, A
	set 6, B
	set 6, C
	set 6, D
	set 6, E
	set 6, H
	set 6, L
	set 6, (HL)
	set 6, A
	set 7, B
	set 7, C
	set 7, D
	set 7, E
	set 7, H
	set 7, L
	set 7, (HL)
	set 7, A
	bit 0, B
	bit 0, C
	bit 0, D
	bit 0, E
	bit 0, H
	bit 0, L
	bit 0, (HL)
	bit 0, A
	bit 1, B
	bit 1, C
	bit 1, D
	bit 1, E
	bit 1, H
	bit 1, L
	bit 1, (HL)
	bit 1, A
	bit 2, B
	bit 2, C
	bit 2, D
	bit 2, E
	bit 2, H
	bit 2, L
	bit 2, (HL)
	bit 2, A
	bit 3, B
	bit 3, C
	bit 3, D
	bit 3, E
	bit 3, H
	bit 3, L
	bit 3, (HL)
	bit 3, A
	bit 4, B
	bit 4, C
	bit 4, D
	bit 4, E
	bit 4, H
	bit 4, L
	bit 4, (HL)
	bit 4, A
	bit 5, B
	bit 5, C
	bit 5, D
	bit 5, E
	bit 5, H
	bit 5, L
	bit 5, (HL)
	bit 5, A
	bit 6, B
	bit 6, C
	bit 6, D
	bit 6, E
	bit 6, H
	bit 6, L
	bit 6, (HL)
	bit 6, A
	bit 7, B
	bit 7, C
	bit 7, D
	bit 7, E
	bit 7, H
	bit 7, L
	bit 7, (HL)
	bit 7, A
	scf
	ccf
	rlc B
	rlc C
	rlc D
	rlc E
	rlc H
	rlc L
	rlc (HL)
	rlc A
	rrc B
	rrc C
	rrc D
	rrc E
	rrc H
	rrc L
	rrc (HL)
	rrc A
	rl B
	rl C
	rl D
	rl E
	rl H
	rl L
	rl (HL)
	rl A
	rr B
	rr C
	rr D
	rr E
	rr H
	rr L
	rr (HL)
	rr A
	sla B
	sla C
	sla D
	sla E
	sla H
	sla L
	sla (HL)
	sla A
	sra B
	sra C
	sra D
	sra E
	sra H
	sra L
	sra (HL)
	sra A
	srl B
	srl C
	srl D
	srl E
	srl H
	srl L
	srl (HL)
	srl A
	rlca
	rrca
	rla
	rra
	rld
	cp B
	cp C
	cp D
	cp E
	cp H
	cp L
	cp (HL)
	cp A
	cp 01
rot:	cp 0feH
	djnz rot
	jp NZ, 08fdH
	jp NZ, 0dcfeH
	jp Z, 08fdH
	jp Z, 0dcfeH
	jp NC, 08fdH
	jp NC, 0dcfeH
	jp C, 08fdH
	jp C, 0dcfeH
	jp PO, 08fdH
	jp PO, 0dcfeH
	jp PE, 08fdH
	jp PE, 0dcfeH
	jp P, 08fdH
	jp P, 0dcfeH
	jp M, 08fdH
	jp M, 0dcfeH
	jp 08fdH
	jp 0dcfeH
rot2:	jp (HL)
	jr NZ, rot2
	jr Z, rot2
	jr NC, rot2
	jr C, rot2
	jr rot2
	push BC
	push DE
	push HL
	push AF
	pop BC
	pop DE
	pop HL
	pop AF
	call 08fdH
	call 0dcfeH
	call NZ, 08fdH
	call NZ, 0dcfeH
	call Z, 08fdH
	call Z, 0dcfeH
	call NC, 08fdH
	call NC, 0dcfeH
	call C, 08fdH
	call C, 0dcfeH
	call PO, 08fdH
	call PO, 0dcfeH
	call PE, 08fdH
	call PE, 0dcfeH
	call P, 08fdH
	call P, 0dcfeH
	call M, 08fdH
	call M, 0dcfeH
	ret
	ret NZ
	ret Z
	ret NC
	ret C
	ret PO
	ret PE
	ret P
	ret M
	in A, (01)
	in A, (0feH)
	out (01), A
	out (0feH), A
	nop
	rst 00h
	rst 08h
	rst 10h
	rst 18h
	rst 20h
	rst 28h
	rst 30h
	rst 38h
	ld IX, (08fdH)
	ld IX, (0dcfeH)
	ld (08fdH), IX
	ld (0dcfeH), IX
	ld B, (IX+78)
	ld C, (IX+78)
	ld D, (IX+78)
	ld E, (IX+78)
	ld H, (IX+78)
	ld L, (IX+78)
	ld (IX+78), B
	ld (IX+78), C
	ld (IX+78), D
	ld (IX+78), E
	ld (IX+78), H
	ld (IX+78), L
	ld (IX+78), A
	ld A, (IX+78)
	ld (IX+78), 01
	ld (IX+78), 0feH
	ld SP, IX
	ld HL, (08fdH)
	ld HL, (0dcfeH)
	ld (08fdH), HL
	ld (0dcfeH), HL
	ld IX, 08fdH
	ld IX, 0dcfeH
	and (IX+78)
	or (IX+78)
	xor (IX+78)
	dec (IX+78)
	dec IX
	inc (IX+78)
	inc IX
	sbc A, (IX+78)
	sub (IX+78)
	adc A, (IX+78)
	add A, (IX+78)
	add IX, BC
	add IX, DE
	add IX, IX
	add IX, SP
	res 0, (IX+78)
	res 1, (IX+78)
	res 2, (IX+78)
	res 3, (IX+78)
	res 4, (IX+78)
	res 5, (IX+78)
	res 6, (IX+78)
	res 7, (IX+78)
	set 0, (IX+78)
	set 1, (IX+78)
	set 2, (IX+78)
	set 3, (IX+78)
	set 4, (IX+78)
	set 5, (IX+78)
	set 6, (IX+78)
	set 7, (IX+78)
	bit 0, (IX+78)
	bit 1, (IX+78)
	bit 2, (IX+78)
	bit 3, (IX+78)
	bit 4, (IX+78)
	bit 5, (IX+78)
	bit 6, (IX+78)
	bit 7, (IX+78)
	rlc (IX+78)
	rrc (IX+78)
	rl (IX+78)
	rr (IX+78)
	sla (IX+78)
	sra (IX+78)
	srl (IX+78)
	cp (IX+78)
	push IX
	pop IX
	ld IY, (08fdH)
	ld IY, (0dcfeH)
	ld (08fdH), IY
	ld (0dcfeH), IY
	ld B, (IY+78)
	ld C, (IY+78)
	ld D, (IY+78)
	ld E, (IY+78)
	ld H, (IY+78)
	ld L, (IY+78)
	ld (IY+78), B
	ld (IY+78), C
	ld (IY+78), D
	ld (IY+78), E
	ld (IY+78), H
	ld (IY+78), L
	ld (IY+78), A
	ld A, (IY+78)
	ld (IY+78), 01
	ld (IY+78), 0feH
	ld SP, IY
	ld HL, (08fdH)
	ld HL, (0dcfeH)
	ld (08fdH), HL
	ld (0dcfeH), HL
	ld IY, 08fdH
	ld IY, 0dcfeH
	and (IY+78)
	or (IY+78)
	xor (IY+78)
	dec (IY+78)
	dec IY
	inc (IY+78)
	inc IY
	sbc A, (IY+78)
	sub (IY+78)
	adc A, (IY+78)
	add A, (IY+78)
	add IY, BC
	add IY, DE
	add IY, IY
	add IY, SP
	res 0, (IY+78)
	res 1, (IY+78)
	res 2, (IY+78)
	res 3, (IY+78)
	res 4, (IY+78)
	res 5, (IY+78)
	res 6, (IY+78)
	res 7, (IY+78)
	set 0, (IY+78)
	set 1, (IY+78)
	set 2, (IY+78)
	set 3, (IY+78)
	set 4, (IY+78)
	set 5, (IY+78)
	set 6, (IY+78)
	set 7, (IY+78)
	bit 0, (IY+78)
	bit 1, (IY+78)
	bit 2, (IY+78)
	bit 3, (IY+78)
	bit 4, (IY+78)
	bit 5, (IY+78)
	bit 6, (IY+78)
	bit 7, (IY+78)
	rlc (IY+78)
	rrc (IY+78)
	rl (IY+78)
	rr (IY+78)
	sla (IY+78)
	sra (IY+78)
	srl (IY+78)
	cp (IY+78)
	push IY
	pop IY
	
